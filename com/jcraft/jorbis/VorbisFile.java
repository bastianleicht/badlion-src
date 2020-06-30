package com.jcraft.jorbis;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;
import com.jcraft.jorbis.JOrbisException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class VorbisFile {
   static final int CHUNKSIZE = 8500;
   static final int SEEK_SET = 0;
   static final int SEEK_CUR = 1;
   static final int SEEK_END = 2;
   static final int OV_FALSE = -1;
   static final int OV_EOF = -2;
   static final int OV_HOLE = -3;
   static final int OV_EREAD = -128;
   static final int OV_EFAULT = -129;
   static final int OV_EIMPL = -130;
   static final int OV_EINVAL = -131;
   static final int OV_ENOTVORBIS = -132;
   static final int OV_EBADHEADER = -133;
   static final int OV_EVERSION = -134;
   static final int OV_ENOTAUDIO = -135;
   static final int OV_EBADPACKET = -136;
   static final int OV_EBADLINK = -137;
   static final int OV_ENOSEEK = -138;
   InputStream datasource;
   boolean seekable = false;
   long offset;
   long end;
   SyncState oy = new SyncState();
   int links;
   long[] offsets;
   long[] dataoffsets;
   int[] serialnos;
   long[] pcmlengths;
   Info[] vi;
   Comment[] vc;
   long pcm_offset;
   boolean decode_ready = false;
   int current_serialno;
   int current_link;
   float bittrack;
   float samptrack;
   StreamState os = new StreamState();
   DspState vd = new DspState();
   Block vb;

   public VorbisFile(String file) throws JOrbisException {
      this.vb = new Block(this.vd);
      InputStream is = null;

      try {
         is = new VorbisFile.SeekableInputStream(file);
         int ret = this.open(is, (byte[])null, 0);
         if(ret == -1) {
            throw new JOrbisException("VorbisFile: open return -1");
         }
      } catch (Exception var11) {
         throw new JOrbisException("VorbisFile: " + var11.toString());
      } finally {
         if(is != null) {
            try {
               is.close();
            } catch (IOException var10) {
               var10.printStackTrace();
            }
         }

      }

   }

   public VorbisFile(InputStream is, byte[] initial, int ibytes) throws JOrbisException {
      this.vb = new Block(this.vd);
      int ret = this.open(is, initial, ibytes);
      if(ret == -1) {
         ;
      }

   }

   private int get_data() {
      int index = this.oy.buffer(8500);
      byte[] buffer = this.oy.data;
      int bytes = 0;

      try {
         bytes = this.datasource.read(buffer, index, 8500);
      } catch (Exception var5) {
         return -128;
      }

      this.oy.wrote(bytes);
      if(bytes == -1) {
         bytes = 0;
      }

      return bytes;
   }

   private void seek_helper(long offst) {
      fseek(this.datasource, offst, 0);
      this.offset = offst;
      this.oy.reset();
   }

   private int get_next_page(Page page, long boundary) {
      if(boundary > 0L) {
         boundary += this.offset;
      }

      while(boundary <= 0L || this.offset < boundary) {
         int more = this.oy.pageseek(page);
         if(more < 0) {
            this.offset -= (long)more;
         } else {
            if(more != 0) {
               int ret = (int)this.offset;
               this.offset += (long)more;
               return ret;
            }

            if(boundary == 0L) {
               return -1;
            }

            int ret = this.get_data();
            if(ret == 0) {
               return -2;
            }

            if(ret < 0) {
               return -128;
            }
         }
      }

      return -1;
   }

   private int get_prev_page(Page page) throws JOrbisException {
      long begin = this.offset;
      int offst = -1;

      while(offst == -1) {
         begin -= 8500L;
         if(begin < 0L) {
            begin = 0L;
         }

         this.seek_helper(begin);

         while(this.offset < begin + 8500L) {
            int ret = this.get_next_page(page, begin + 8500L - this.offset);
            if(ret == -128) {
               return -128;
            }

            if(ret < 0) {
               if(offst == -1) {
                  throw new JOrbisException();
               }
               break;
            }

            offst = ret;
         }
      }

      this.seek_helper((long)offst);
      int ret = this.get_next_page(page, 8500L);
      if(ret < 0) {
         return -129;
      } else {
         return offst;
      }
   }

   int bisect_forward_serialno(long begin, long searched, long end, int currentno, int m) {
      long endsearched = end;
      long next = end;
      Page page = new Page();

      while(searched < endsearched) {
         long bisect;
         if(endsearched - searched < 8500L) {
            bisect = searched;
         } else {
            bisect = (searched + endsearched) / 2L;
         }

         this.seek_helper(bisect);
         int ret = this.get_next_page(page, -1L);
         if(ret == -128) {
            return -128;
         }

         if(ret >= 0 && page.serialno() == currentno) {
            searched = (long)(ret + page.header_len + page.body_len);
         } else {
            endsearched = bisect;
            if(ret >= 0) {
               next = (long)ret;
            }
         }
      }

      this.seek_helper(next);
      int ret = this.get_next_page(page, -1L);
      if(ret == -128) {
         return -128;
      } else {
         if(searched < end && ret != -1) {
            ret = this.bisect_forward_serialno(next, this.offset, end, page.serialno(), m + 1);
            if(ret == -128) {
               return -128;
            }
         } else {
            this.links = m + 1;
            this.offsets = new long[m + 2];
            this.offsets[m + 1] = searched;
         }

         this.offsets[m] = begin;
         return 0;
      }
   }

   int fetch_headers(Info vi, Comment vc, int[] serialno, Page og_ptr) {
      Page og = new Page();
      Packet op = new Packet();
      if(og_ptr == null) {
         int ret = this.get_next_page(og, 8500L);
         if(ret == -128) {
            return -128;
         }

         if(ret < 0) {
            return -132;
         }

         og_ptr = og;
      }

      if(serialno != null) {
         serialno[0] = og_ptr.serialno();
      }

      this.os.init(og_ptr.serialno());
      vi.init();
      vc.init();
      int i = 0;

      while(i < 3) {
         this.os.pagein(og_ptr);

         while(i < 3) {
            int result = this.os.packetout(op);
            if(result == 0) {
               break;
            }

            if(result == -1) {
               vi.clear();
               vc.clear();
               this.os.clear();
               return -1;
            }

            if(vi.synthesis_headerin(vc, op) != 0) {
               vi.clear();
               vc.clear();
               this.os.clear();
               return -1;
            }

            ++i;
         }

         if(i < 3 && this.get_next_page(og_ptr, 1L) < 0) {
            vi.clear();
            vc.clear();
            this.os.clear();
            return -1;
         }
      }

      return 0;
   }

   void prefetch_all_headers(Info first_i, Comment first_c, int dataoffset) throws JOrbisException {
      Page og = new Page();
      this.vi = new Info[this.links];
      this.vc = new Comment[this.links];
      this.dataoffsets = new long[this.links];
      this.pcmlengths = new long[this.links];
      this.serialnos = new int[this.links];

      label73:
      for(int i = 0; i < this.links; ++i) {
         if(first_i != null && first_c != null && i == 0) {
            this.vi[i] = first_i;
            this.vc[i] = first_c;
            this.dataoffsets[i] = (long)dataoffset;
         } else {
            this.seek_helper(this.offsets[i]);
            this.vi[i] = new Info();
            this.vc[i] = new Comment();
            if(this.fetch_headers(this.vi[i], this.vc[i], (int[])null, (Page)null) == -1) {
               this.dataoffsets[i] = -1L;
            } else {
               this.dataoffsets[i] = this.offset;
               this.os.clear();
            }
         }

         long end = this.offsets[i + 1];
         this.seek_helper(end);

         while(true) {
            int ret = this.get_prev_page(og);
            if(ret == -1) {
               this.vi[i].clear();
               this.vc[i].clear();
               continue label73;
            }

            if(og.granulepos() != -1L) {
               break;
            }
         }

         this.serialnos[i] = og.serialno();
         this.pcmlengths[i] = og.granulepos();
      }

   }

   private int make_decode_ready() {
      if(this.decode_ready) {
         System.exit(1);
      }

      this.vd.synthesis_init(this.vi[0]);
      this.vb.init(this.vd);
      this.decode_ready = true;
      return 0;
   }

   int open_seekable() throws JOrbisException {
      Info initial_i = new Info();
      Comment initial_c = new Comment();
      Page og = new Page();
      int[] foo = new int[1];
      int ret = this.fetch_headers(initial_i, initial_c, foo, (Page)null);
      int serialno = foo[0];
      int dataoffset = (int)this.offset;
      this.os.clear();
      if(ret == -1) {
         return -1;
      } else if(ret < 0) {
         return ret;
      } else {
         this.seekable = true;
         fseek(this.datasource, 0L, 2);
         this.offset = ftell(this.datasource);
         long end = this.offset;
         end = (long)this.get_prev_page(og);
         if(og.serialno() != serialno) {
            if(this.bisect_forward_serialno(0L, 0L, end + 1L, serialno, 0) < 0) {
               this.clear();
               return -128;
            }
         } else if(this.bisect_forward_serialno(0L, end, end + 1L, serialno, 0) < 0) {
            this.clear();
            return -128;
         }

         this.prefetch_all_headers(initial_i, initial_c, dataoffset);
         return 0;
      }
   }

   int open_nonseekable() {
      this.links = 1;
      this.vi = new Info[this.links];
      this.vi[0] = new Info();
      this.vc = new Comment[this.links];
      this.vc[0] = new Comment();
      int[] foo = new int[1];
      if(this.fetch_headers(this.vi[0], this.vc[0], foo, (Page)null) == -1) {
         return -1;
      } else {
         this.current_serialno = foo[0];
         this.make_decode_ready();
         return 0;
      }
   }

   void decode_clear() {
      this.os.clear();
      this.vd.clear();
      this.vb.clear();
      this.decode_ready = false;
      this.bittrack = 0.0F;
      this.samptrack = 0.0F;
   }

   int process_packet(int readp) {
      Page og = new Page();

      while(true) {
         if(this.decode_ready) {
            Packet op = new Packet();
            int result = this.os.packetout(op);
            if(result > 0) {
               long granulepos = op.granulepos;
               if(this.vb.synthesis(op) == 0) {
                  int oldsamples = this.vd.synthesis_pcmout((float[][][])null, (int[])null);
                  this.vd.synthesis_blockin(this.vb);
                  this.samptrack += (float)(this.vd.synthesis_pcmout((float[][][])null, (int[])null) - oldsamples);
                  this.bittrack += (float)(op.bytes * 8);
                  if(granulepos != -1L && op.e_o_s == 0) {
                     oldsamples = this.seekable?this.current_link:0;
                     int samples = this.vd.synthesis_pcmout((float[][][])null, (int[])null);
                     granulepos = granulepos - (long)samples;

                     for(int i = 0; i < oldsamples; ++i) {
                        granulepos += this.pcmlengths[i];
                     }

                     this.pcm_offset = granulepos;
                  }

                  return 1;
               }
            }
         }

         if(readp == 0) {
            return 0;
         }

         if(this.get_next_page(og, -1L) < 0) {
            return 0;
         }

         this.bittrack += (float)(og.header_len * 8);
         if(this.decode_ready && this.current_serialno != og.serialno()) {
            this.decode_clear();
         }

         if(!this.decode_ready) {
            if(!this.seekable) {
               int[] foo = new int[1];
               int ret = this.fetch_headers(this.vi[0], this.vc[0], foo, og);
               this.current_serialno = foo[0];
               if(ret != 0) {
                  return ret;
               }

               ++this.current_link;
               int i = 0;
            } else {
               this.current_serialno = og.serialno();

               int i;
               for(i = 0; i < this.links && this.serialnos[i] != this.current_serialno; ++i) {
                  ;
               }

               if(i == this.links) {
                  return -1;
               }

               this.current_link = i;
               this.os.init(this.current_serialno);
               this.os.reset();
            }

            this.make_decode_ready();
         }

         this.os.pagein(og);
      }
   }

   int clear() {
      this.vb.clear();
      this.vd.clear();
      this.os.clear();
      if(this.vi != null && this.links != 0) {
         for(int i = 0; i < this.links; ++i) {
            this.vi[i].clear();
            this.vc[i].clear();
         }

         this.vi = null;
         this.vc = null;
      }

      if(this.dataoffsets != null) {
         this.dataoffsets = null;
      }

      if(this.pcmlengths != null) {
         this.pcmlengths = null;
      }

      if(this.serialnos != null) {
         this.serialnos = null;
      }

      if(this.offsets != null) {
         this.offsets = null;
      }

      this.oy.clear();
      return 0;
   }

   static int fseek(InputStream fis, long off, int whence) {
      if(fis instanceof VorbisFile.SeekableInputStream) {
         VorbisFile.SeekableInputStream sis = (VorbisFile.SeekableInputStream)fis;

         try {
            if(whence == 0) {
               sis.seek(off);
            } else if(whence == 2) {
               sis.seek(sis.getLength() - off);
            }
         } catch (Exception var6) {
            ;
         }

         return 0;
      } else {
         try {
            if(whence == 0) {
               fis.reset();
            }

            fis.skip(off);
            return 0;
         } catch (Exception var7) {
            return -1;
         }
      }
   }

   static long ftell(InputStream fis) {
      try {
         if(fis instanceof VorbisFile.SeekableInputStream) {
            VorbisFile.SeekableInputStream sis = (VorbisFile.SeekableInputStream)fis;
            return sis.tell();
         }
      } catch (Exception var2) {
         ;
      }

      return 0L;
   }

   int open(InputStream is, byte[] initial, int ibytes) throws JOrbisException {
      return this.open_callbacks(is, initial, ibytes);
   }

   int open_callbacks(InputStream is, byte[] initial, int ibytes) throws JOrbisException {
      this.datasource = is;
      this.oy.init();
      if(initial != null) {
         int index = this.oy.buffer(ibytes);
         System.arraycopy(initial, 0, this.oy.data, index, ibytes);
         this.oy.wrote(ibytes);
      }

      int ret;
      if(is instanceof VorbisFile.SeekableInputStream) {
         ret = this.open_seekable();
      } else {
         ret = this.open_nonseekable();
      }

      if(ret != 0) {
         this.datasource = null;
         this.clear();
      }

      return ret;
   }

   public int streams() {
      return this.links;
   }

   public boolean seekable() {
      return this.seekable;
   }

   public int bitrate(int i) {
      if(i >= this.links) {
         return -1;
      } else if(!this.seekable && i != 0) {
         return this.bitrate(0);
      } else if(i >= 0) {
         return this.seekable?(int)Math.rint((double)((float)((this.offsets[i + 1] - this.dataoffsets[i]) * 8L) / this.time_total(i))):(this.vi[i].bitrate_nominal > 0?this.vi[i].bitrate_nominal:(this.vi[i].bitrate_upper > 0?(this.vi[i].bitrate_lower > 0?(this.vi[i].bitrate_upper + this.vi[i].bitrate_lower) / 2:this.vi[i].bitrate_upper):-1));
      } else {
         long bits = 0L;

         for(int j = 0; j < this.links; ++j) {
            bits += (this.offsets[j + 1] - this.dataoffsets[j]) * 8L;
         }

         return (int)Math.rint((double)((float)bits / this.time_total(-1)));
      }
   }

   public int bitrate_instant() {
      int _link = this.seekable?this.current_link:0;
      if(this.samptrack == 0.0F) {
         return -1;
      } else {
         int ret = (int)((double)(this.bittrack / this.samptrack * (float)this.vi[_link].rate) + 0.5D);
         this.bittrack = 0.0F;
         this.samptrack = 0.0F;
         return ret;
      }
   }

   public int serialnumber(int i) {
      return i >= this.links?-1:(!this.seekable && i >= 0?this.serialnumber(-1):(i < 0?this.current_serialno:this.serialnos[i]));
   }

   public long raw_total(int i) {
      if(this.seekable && i < this.links) {
         if(i >= 0) {
            return this.offsets[i + 1] - this.offsets[i];
         } else {
            long acc = 0L;

            for(int j = 0; j < this.links; ++j) {
               acc += this.raw_total(j);
            }

            return acc;
         }
      } else {
         return -1L;
      }
   }

   public long pcm_total(int i) {
      if(this.seekable && i < this.links) {
         if(i >= 0) {
            return this.pcmlengths[i];
         } else {
            long acc = 0L;

            for(int j = 0; j < this.links; ++j) {
               acc += this.pcm_total(j);
            }

            return acc;
         }
      } else {
         return -1L;
      }
   }

   public float time_total(int i) {
      if(this.seekable && i < this.links) {
         if(i >= 0) {
            return (float)this.pcmlengths[i] / (float)this.vi[i].rate;
         } else {
            float acc = 0.0F;

            for(int j = 0; j < this.links; ++j) {
               acc += this.time_total(j);
            }

            return acc;
         }
      } else {
         return -1.0F;
      }
   }

   public int raw_seek(int pos) {
      if(!this.seekable) {
         return -1;
      } else if(pos >= 0 && (long)pos <= this.offsets[this.links]) {
         this.pcm_offset = -1L;
         this.decode_clear();
         this.seek_helper((long)pos);
         switch(this.process_packet(1)) {
         case -1:
            this.pcm_offset = -1L;
            this.decode_clear();
            return -1;
         case 0:
            this.pcm_offset = this.pcm_total(-1);
            return 0;
         default:
            while(true) {
               switch(this.process_packet(0)) {
               case -1:
                  this.pcm_offset = -1L;
                  this.decode_clear();
                  return -1;
               case 0:
                  return 0;
               }
            }
         }
      } else {
         this.pcm_offset = -1L;
         this.decode_clear();
         return -1;
      }
   }

   public int pcm_seek(long pos) {
      int link = -1;
      long total = this.pcm_total(-1);
      if(!this.seekable) {
         return -1;
      } else if(pos >= 0L && pos <= total) {
         for(link = this.links - 1; link >= 0; --link) {
            total -= this.pcmlengths[link];
            if(pos >= total) {
               break;
            }
         }

         long target = pos - total;
         long end = this.offsets[link + 1];
         long begin = this.offsets[link];
         int best = (int)begin;
         Page og = new Page();

         while(begin < end) {
            long bisect;
            if(end - begin < 8500L) {
               bisect = begin;
            } else {
               bisect = (end + begin) / 2L;
            }

            this.seek_helper(bisect);
            int ret = this.get_next_page(og, end - bisect);
            if(ret == -1) {
               end = bisect;
            } else {
               long granulepos = og.granulepos();
               if(granulepos < target) {
                  best = ret;
                  begin = this.offset;
               } else {
                  end = bisect;
               }
            }
         }

         if(this.raw_seek(best) != 0) {
            this.pcm_offset = -1L;
            this.decode_clear();
            return -1;
         } else if(this.pcm_offset >= pos) {
            this.pcm_offset = -1L;
            this.decode_clear();
            return -1;
         } else if(pos > this.pcm_total(-1)) {
            this.pcm_offset = -1L;
            this.decode_clear();
            return -1;
         } else {
            while(this.pcm_offset < pos) {
               int target = (int)(pos - this.pcm_offset);
               float[][][] _pcm = new float[1][][];
               int[] _index = new int[this.getInfo(-1).channels];
               int samples = this.vd.synthesis_pcmout(_pcm, _index);
               if(samples > target) {
                  samples = target;
               }

               this.vd.synthesis_read(samples);
               this.pcm_offset += (long)samples;
               if(samples < target && this.process_packet(1) == 0) {
                  this.pcm_offset = this.pcm_total(-1);
               }
            }

            return 0;
         }
      } else {
         this.pcm_offset = -1L;
         this.decode_clear();
         return -1;
      }
   }

   int time_seek(float seconds) {
      int link = -1;
      long pcm_total = this.pcm_total(-1);
      float time_total = this.time_total(-1);
      if(!this.seekable) {
         return -1;
      } else if(seconds >= 0.0F && seconds <= time_total) {
         for(link = this.links - 1; link >= 0; --link) {
            pcm_total -= this.pcmlengths[link];
            time_total -= this.time_total(link);
            if(seconds >= time_total) {
               break;
            }
         }

         long target = (long)((float)pcm_total + (seconds - time_total) * (float)this.vi[link].rate);
         return this.pcm_seek(target);
      } else {
         this.pcm_offset = -1L;
         this.decode_clear();
         return -1;
      }
   }

   public long raw_tell() {
      return this.offset;
   }

   public long pcm_tell() {
      return this.pcm_offset;
   }

   public float time_tell() {
      int link = -1;
      long pcm_total = 0L;
      float time_total = 0.0F;
      if(this.seekable) {
         pcm_total = this.pcm_total(-1);
         time_total = this.time_total(-1);

         for(link = this.links - 1; link >= 0; --link) {
            pcm_total -= this.pcmlengths[link];
            time_total -= this.time_total(link);
            if(this.pcm_offset >= pcm_total) {
               break;
            }
         }
      }

      return time_total + (float)(this.pcm_offset - pcm_total) / (float)this.vi[link].rate;
   }

   public Info getInfo(int link) {
      return this.seekable?(link < 0?(this.decode_ready?this.vi[this.current_link]:null):(link >= this.links?null:this.vi[link])):(this.decode_ready?this.vi[0]:null);
   }

   public Comment getComment(int link) {
      return this.seekable?(link < 0?(this.decode_ready?this.vc[this.current_link]:null):(link >= this.links?null:this.vc[link])):(this.decode_ready?this.vc[0]:null);
   }

   int host_is_big_endian() {
      return 1;
   }

   int read(byte[] buffer, int length, int bigendianp, int word, int sgned, int[] bitstream) {
      int host_endian = this.host_is_big_endian();
      int index = 0;

      while(true) {
         if(this.decode_ready) {
            float[][][] _pcm = new float[1][][];
            int[] _index = new int[this.getInfo(-1).channels];
            int samples = this.vd.synthesis_pcmout(_pcm, _index);
            float[][] pcm = _pcm[0];
            if(samples != 0) {
               int channels = this.getInfo(-1).channels;
               int bytespersample = word * channels;
               if(samples > length / bytespersample) {
                  samples = length / bytespersample;
               }

               if(word == 1) {
                  int off = sgned != 0?0:128;

                  for(int j = 0; j < samples; ++j) {
                     for(int i = 0; i < channels; ++i) {
                        int val = (int)((double)pcm[i][_index[i] + j] * 128.0D + 0.5D);
                        if(val > 127) {
                           val = 127;
                        } else if(val < -128) {
                           val = -128;
                        }

                        buffer[index++] = (byte)(val + off);
                     }
                  }
               } else {
                  int off = sgned != 0?0:'耀';
                  if(host_endian == bigendianp) {
                     if(sgned != 0) {
                        for(int i = 0; i < channels; ++i) {
                           int src = _index[i];
                           int dest = i;

                           for(int j = 0; j < samples; ++j) {
                              int val = (int)((double)pcm[i][src + j] * 32768.0D + 0.5D);
                              if(val > 32767) {
                                 val = 32767;
                              } else if(val < -32768) {
                                 val = -32768;
                              }

                              buffer[dest] = (byte)(val >>> 8);
                              buffer[dest + 1] = (byte)val;
                              dest += channels * 2;
                           }
                        }
                     } else {
                        for(int i = 0; i < channels; ++i) {
                           float[] src = pcm[i];
                           int dest = i;

                           for(int j = 0; j < samples; ++j) {
                              int val = (int)((double)src[j] * 32768.0D + 0.5D);
                              if(val > 32767) {
                                 val = 32767;
                              } else if(val < -32768) {
                                 val = -32768;
                              }

                              buffer[dest] = (byte)(val + off >>> 8);
                              buffer[dest + 1] = (byte)(val + off);
                              dest += channels * 2;
                           }
                        }
                     }
                  } else if(bigendianp != 0) {
                     for(int j = 0; j < samples; ++j) {
                        for(int i = 0; i < channels; ++i) {
                           int val = (int)((double)pcm[i][j] * 32768.0D + 0.5D);
                           if(val > 32767) {
                              val = 32767;
                           } else if(val < -32768) {
                              val = -32768;
                           }

                           val = val + off;
                           buffer[index++] = (byte)(val >>> 8);
                           buffer[index++] = (byte)val;
                        }
                     }
                  } else {
                     for(int j = 0; j < samples; ++j) {
                        for(int i = 0; i < channels; ++i) {
                           int val = (int)((double)pcm[i][j] * 32768.0D + 0.5D);
                           if(val > 32767) {
                              val = 32767;
                           } else if(val < -32768) {
                              val = -32768;
                           }

                           val = val + off;
                           buffer[index++] = (byte)val;
                           buffer[index++] = (byte)(val >>> 8);
                        }
                     }
                  }
               }

               this.vd.synthesis_read(samples);
               this.pcm_offset += (long)samples;
               if(bitstream != null) {
                  bitstream[0] = this.current_link;
               }

               return samples * bytespersample;
            }
         }

         switch(this.process_packet(1)) {
         case -1:
            return -1;
         case 0:
            return 0;
         }
      }
   }

   public Info[] getInfo() {
      return this.vi;
   }

   public Comment[] getComment() {
      return this.vc;
   }

   public void close() throws IOException {
      this.datasource.close();
   }

   class SeekableInputStream extends InputStream {
      RandomAccessFile raf = null;
      final String mode = "r";

      SeekableInputStream(String file) throws IOException {
         this.raf = new RandomAccessFile(file, "r");
      }

      public int read() throws IOException {
         return this.raf.read();
      }

      public int read(byte[] buf) throws IOException {
         return this.raf.read(buf);
      }

      public int read(byte[] buf, int s, int len) throws IOException {
         return this.raf.read(buf, s, len);
      }

      public long skip(long n) throws IOException {
         return (long)this.raf.skipBytes((int)n);
      }

      public long getLength() throws IOException {
         return this.raf.length();
      }

      public long tell() throws IOException {
         return this.raf.getFilePointer();
      }

      public int available() throws IOException {
         return this.raf.length() == this.raf.getFilePointer()?0:1;
      }

      public void close() throws IOException {
         this.raf.close();
      }

      public synchronized void mark(int m) {
      }

      public synchronized void reset() throws IOException {
      }

      public boolean markSupported() {
         return false;
      }

      public void seek(long pos) throws IOException {
         this.raf.seek(pos);
      }
   }
}
