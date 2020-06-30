package org.apache.commons.compress.archivers.zip;

import java.util.zip.ZipException;
import org.apache.commons.compress.archivers.zip.ZipShort;

public interface ZipExtraField {
   ZipShort getHeaderId();

   ZipShort getLocalFileDataLength();

   ZipShort getCentralDirectoryLength();

   byte[] getLocalFileDataData();

   byte[] getCentralDirectoryData();

   void parseFromLocalFileData(byte[] var1, int var2, int var3) throws ZipException;

   void parseFromCentralDirectoryData(byte[] var1, int var2, int var3) throws ZipException;
}
