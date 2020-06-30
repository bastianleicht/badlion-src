package org.apache.commons.compress.archivers.zip;

import org.apache.commons.compress.archivers.zip.ZipExtraField;
import org.apache.commons.compress.archivers.zip.ZipShort;
import org.apache.commons.compress.archivers.zip.ZipUtil;

public class UnrecognizedExtraField implements ZipExtraField {
   private ZipShort headerId;
   private byte[] localData;
   private byte[] centralData;

   public void setHeaderId(ZipShort headerId) {
      this.headerId = headerId;
   }

   public ZipShort getHeaderId() {
      return this.headerId;
   }

   public void setLocalFileDataData(byte[] data) {
      this.localData = ZipUtil.copy(data);
   }

   public ZipShort getLocalFileDataLength() {
      return new ZipShort(this.localData != null?this.localData.length:0);
   }

   public byte[] getLocalFileDataData() {
      return ZipUtil.copy(this.localData);
   }

   public void setCentralDirectoryData(byte[] data) {
      this.centralData = ZipUtil.copy(data);
   }

   public ZipShort getCentralDirectoryLength() {
      return this.centralData != null?new ZipShort(this.centralData.length):this.getLocalFileDataLength();
   }

   public byte[] getCentralDirectoryData() {
      return this.centralData != null?ZipUtil.copy(this.centralData):this.getLocalFileDataData();
   }

   public void parseFromLocalFileData(byte[] data, int offset, int length) {
      byte[] tmp = new byte[length];
      System.arraycopy(data, offset, tmp, 0, length);
      this.setLocalFileDataData(tmp);
   }

   public void parseFromCentralDirectoryData(byte[] data, int offset, int length) {
      byte[] tmp = new byte[length];
      System.arraycopy(data, offset, tmp, 0, length);
      this.setCentralDirectoryData(tmp);
      if(this.localData == null) {
         this.setLocalFileDataData(tmp);
      }

   }
}
