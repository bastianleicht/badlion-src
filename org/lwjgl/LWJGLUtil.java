package org.lwjgl;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.lwjgl.BufferUtils;

public class LWJGLUtil {
   public static final int PLATFORM_LINUX = 1;
   public static final int PLATFORM_MACOSX = 2;
   public static final int PLATFORM_WINDOWS = 3;
   public static final String PLATFORM_LINUX_NAME = "linux";
   public static final String PLATFORM_MACOSX_NAME = "macosx";
   public static final String PLATFORM_WINDOWS_NAME = "windows";
   private static final String LWJGL_ICON_DATA_16x16 = "ÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿþÿÿÿÂ×èÿt¤ËÿP\u008b½ÿT\u008e¿ÿ\u0086¯Òÿçïöÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿõõõÿ\u008d\u008f\u0091ÿv\u0082\u008dÿ}\u008d\u009bÿ\u0084\u0099ªÿ\u0094·Õÿ:}µÿH\u0086ºÿÚçñÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿôøûÿ\u009c\u009e ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿäääÿ\u0084\u00adÐÿ:}µÿ[\u0092Áÿüýþÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿ\u0091¶Õÿ___ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\"\"\"ÿÿÿÿÿèðöÿ9|µÿ:}µÿÄØéÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÆÙéÿ\u0081«Îÿ\u001d\u001d\u001dÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿfffÿÿÿÿÿÐàíÿ:}µÿ:}µÿ\u008d´Ôÿÿÿÿÿòòòÿ¥¥¥ÿßßßÿ¢ÁÜÿ°ÅÖÿ\n\n\nÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ¨¨¨ÿÿÿÿÿ\u0097ºØÿ:}µÿ:}µÿq¡Êÿÿÿÿÿ¡¡¡ÿ\u0000\u0000\u0000ÿ\u0001\u0001\u0001ÿ###ÿÌÌÌÿÐÐÐÿ¥¥¥ÿ\u0084\u0084\u0084ÿ\\\\\\ÿïïïÿÿÿÿÿ`\u0096Ãÿ:}µÿ:}µÿm\u009eÈÿÿÿÿÿ^^^ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿÏÏÏÿ\u001f\u001f\u001fÿ\u0003\u0003\u0003ÿ+++ÿlllÿÆÆÆÿúüýÿ\u009e¿Úÿw¥ÌÿL\u0089¼ÿ|¨Îÿÿÿÿÿ\u001b\u001b\u001bÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0017\u0017\u0017ÿÖÖÖÿ\u0001\u0001\u0001ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿRRRÿåååÿ===ÿhhhÿ¦¦§ÿÚÞáÿÿÿÿÿtttÿ\u000e\u000e\u000eÿ\u0000\u0000\u0000ÿYYYÿ\u0095\u0095\u0095ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0091\u0091\u0091ÿ\u009b\u009b\u009bÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0002\u0002\u0002ÿCCCÿÿÿÿÿîîîÿ\u008c\u008c\u008cÿ¿¿¿ÿVVVÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿÓÓÓÿXXXÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ333ÿÿÿÿÿÿÿÿÿüýþÿÿÿÿÿÀÀÀÿ@@@ÿ\u0002\u0002\u0002ÿ\u0000\u0000\u0000ÿ\u001b\u001b\u001bÿûûûÿ\u0017\u0017\u0017ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿxxxÿÿÿÿÿÿÿÿÿÒáîÿ~©ÎÿàêóÿÿÿÿÿÔÔÔÿmmmÿ\u0084\u0084\u0084ÿÓÓÓÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ½½½ÿÿÿÿÿÿÿÿÿþþþÿf\u009aÅÿ=\u007f¶ÿ\u0082¬Ðÿäíõÿÿÿÿÿÿÿÿÿåååÿ---ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\f\f\fÿöööÿÿÿÿÿÿÿÿÿÿÿÿÿâìôÿM\u0089¼ÿ:}µÿ>\u0080¶ÿ\u0087°Òÿçïöÿÿÿÿÿþþþÿ¼¼¼ÿOOOÿ\u0003\u0003\u0003ÿJJJÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿëòøÿ\u008c³ÔÿV\u0090ÀÿP\u008c½ÿw¥Ìÿíóøÿÿÿÿÿÿÿÿÿÿÿÿÿßßßÿæææÿÿÿÿÿ";
   private static final String LWJGL_ICON_DATA_32x32 = "ÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿúüýÿËÝìÿ\u0093·Öÿo Éÿ\\\u0093Âÿ`\u0096Ãÿz§Íÿ¬ÈàÿíóøÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿüýþÿµÎãÿZ\u0092Áÿ:}µÿ:}µÿ:}µÿ:}µÿ:}µÿ:}µÿ:}µÿE\u0085¹ÿ¡ÁÜÿüýþÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿüüüÿâêñÿ¸Ïãÿ®Éàÿ£ÂÜÿ\u0098ºØÿ\u008c³Óÿ\u0081¬Ïÿn\u009fÈÿ=\u007f¶ÿ:}µÿ:}µÿ:}µÿr¢ÊÿõøûÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿØØØÿ111ÿ$$$ÿ111ÿ@@@ÿRRRÿeeeÿtttÿ\u008f\u008f\u008fÿ÷øøÿ¬Èßÿ;}µÿ:}µÿ:}µÿ:}µÿy§Íÿüýþÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿþþþÿhhhÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ¶¶¶ÿþþþÿ\u0086®Ñÿ:}µÿ:}µÿ:}µÿ:}µÿ®ÊáÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÓâîÿáêòÿ(((ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0002\u0002\u0002ÿÞÞÞÿÿÿÿÿõøûÿ[\u0092Áÿ:}µÿ:}µÿ:}µÿH\u0086ºÿôøûÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿìóøÿd\u0099Åÿ×ÙÛÿ\u0005\u0005\u0005ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ$$$ÿþþþÿÿÿÿÿÿÿÿÿÀÕçÿ9|µÿ:}µÿ:}µÿ:}µÿ«Èàÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿþÿÿÿx¦Ìÿ{§Íÿ\u009e\u009e\u009eÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿeeeÿÿÿÿÿÿÿÿÿÿÿÿÿâìôÿ7{´ÿ:}µÿ:}µÿ:}µÿf\u009aÅÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÃ×èÿ9}µÿ²ÌáÿXXXÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ©©©ÿÿÿÿÿÿÿÿÿÿÿÿÿ¼Òåÿ:}µÿ:}µÿ:}µÿ:}µÿ@\u0081·ÿîôùÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿúüýÿZ\u0092Áÿ=\u007f¶ÿÝåíÿ\u001c\u001c\u001cÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0007\u0007\u0007ÿçççÿÿÿÿÿÿÿÿÿÿÿÿÿ\u0085®Ñÿ:}µÿ:}µÿ:}µÿ:}µÿ9}µÿÌÞìÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿþþþÿÿÿÿÿÿÿÿÿÿÿÿÿºÒåÿ:}µÿW\u0090¿ÿÐÑÑÿ\u0003\u0003\u0003ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ333ÿýýýÿÿÿÿÿÿÿÿÿûüýÿP\u008b½ÿ:}µÿ:}µÿ:}µÿ:}µÿ9}µÿ±ÌâÿÿÿÿÿÿÿÿÿÿÿÿÿÊÊÊÿ777ÿaaaÿ¢¢¢ÿÝÝÝÿäìóÿ±Ëáÿ´Íâÿæææÿ###ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿrrrÿÿÿÿÿÿÿÿÿÿÿÿÿØåðÿ:}µÿ:}µÿ:}µÿ:}µÿ:}µÿ:}µÿ ÀÛÿÿÿÿÿÿÿÿÿÿÿÿÿfffÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0006\u0006\u0006ÿ\'\'\'ÿfffÿÔÔÔÿÿÿÿÿöööÿÐÐÐÿ\u0097\u0097\u0097ÿ^^^ÿ\'\'\'ÿ\u0004\u0004\u0004ÿ\u0000\u0000\u0000ÿ\u0003\u0003\u0003ÿÀÀÀÿÿÿÿÿÿÿÿÿÿÿÿÿ¢ÁÛÿ:}µÿ:}µÿ:}µÿ:}µÿ:}µÿ:}µÿ\u009e¿Úÿÿÿÿÿÿÿÿÿûûûÿ%%%ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\\\\\\ÿÿÿÿÿêêêÿ\u008f\u008f\u008fÿµµµÿéééÿýýýÿçççÿ²²²ÿ½½½ÿþþþÿÿÿÿÿÿÿÿÿÿÿÿÿk\u009dÇÿ:}µÿ:}µÿ:}µÿ:}µÿ:}µÿ:}µÿ¡ÁÜÿÿÿÿÿÿÿÿÿÛÛÛÿ\u0003\u0003\u0003ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0083\u0083\u0083ÿÿÿÿÿ___ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u000b\u000b\u000bÿ777ÿvvvÿ···ÿðððÿÿÿÿÿÿÿÿÿÿÿÿÿðõùÿK\u0088»ÿ=\u007f¶ÿ9|µÿ:}µÿ:}µÿ:}µÿ:}µÿ²Ìâÿÿÿÿÿÿÿÿÿ\u009a\u009a\u009aÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿÅÅÅÿ÷÷÷ÿ\u001d\u001d\u001dÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0007\u0007\u0007ÿ<<<ÿßßßÿÿÿÿÿûüýÿüýþÿó÷úÿÌÝëÿ\u009e¾Úÿr¡ÉÿL\u0089»ÿ:}µÿÉÜëÿÿÿÿÿÿÿÿÿVVVÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u000f\u000f\u000fÿùùùÿÑÑÑÿ\u0003\u0003\u0003ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u008e\u008e\u008eÿÿÿÿÿùùùÿ\u0084\u0084\u0084ÿpppÿ°°°ÿêêêÿÿÿÿÿùûüÿÜèòÿöùüÿÿÿÿÿÿÿÿÿ\u0015\u0015\u0015ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿNNNÿÿÿÿÿ\u0091\u0091\u0091ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ»»»ÿÿÿÿÿ\u009e\u009e\u009eÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0004\u0004\u0004ÿ///ÿpppÿ°°°ÿæææÿþþþÿÿÿÿÿ999ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0090\u0090\u0090ÿÿÿÿÿMMMÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0010\u0010\u0010ÿðððÿÿÿÿÿZZZÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\t\t\tÿ222ÿ±±±ÿöööÿ¡¡¡ÿ555ÿ\u0002\u0002\u0002ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0002\u0002\u0002ÿÑÑÑÿõõõÿ\u0013\u0013\u0013ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿEEEÿþþþÿøøøÿ\u001d\u001d\u001dÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ+++ÿÿÿÿÿÿÿÿÿüüüÿ¾¾¾ÿPPPÿ\u0005\u0005\u0005ÿ%%%ÿùùùÿÂÂÂÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0086\u0086\u0086ÿÿÿÿÿÒÒÒÿ\u0001\u0001\u0001ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿCCCÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿþþþÿÜÜÜÿàààÿÿÿÿÿ\u0095\u0095\u0095ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0001\u0001\u0001ÿÈÈÈÿÿÿÿÿ\u008e\u008e\u008eÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0088\u0088\u0088ÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿßßßÿ)))ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0018\u0018\u0018ÿõõõÿÿÿÿÿJJJÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿÏÏÏÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿñöúÿÿÿÿÿÿÿÿÿÿÿÿÿùùùÿµµµÿKKKÿ\u0006\u0006\u0006ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿRRRÿÿÿÿÿøøøÿ\u0010\u0010\u0010ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u001c\u001c\u001cÿøøøÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿ\u0086¯Ñÿ\u0090µÕÿêñ÷ÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÛÛÛÿxxxÿ\u001b\u001b\u001bÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0096\u0096\u0096ÿÿÿÿÿÆÆÆÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿZZZÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÃØéÿ:}µÿC\u0083¸ÿ\u0094¸Öÿíóøÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿôôôÿ§§§ÿ\u0085\u0085\u0085ÿôôôÿÿÿÿÿ\u0086\u0086\u0086ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u009d\u009d\u009dÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿúûýÿ]\u0094Âÿ:}µÿ:}µÿF\u0085¹ÿ\u0098»Øÿïôùÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿ\u009b\u009b\u009bÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0005\u0005\u0005ÿÝÝÝÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÅÙéÿ;~¶ÿ:}µÿ:}µÿ:}µÿH\u0086ºÿ\u009e¾Úÿòöúÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿûûûÿ\u008e\u008e\u008eÿ%%%ÿ\u0001\u0001\u0001ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ,,,ÿüüüÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿ\u008fµÕÿ:}µÿ:}µÿ:}µÿ:}µÿ:}µÿJ\u0087»ÿ£ÂÜÿó÷úÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿúúúÿ°°°ÿEEEÿ\u0004\u0004\u0004ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿnnnÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿüýþÿ\u0085¯Ñÿ:}µÿ:}µÿ:}µÿ:}µÿ:}µÿ:}µÿM\u0089¼ÿ¨ÅÞÿöùüÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿþþþÿÒÒÒÿhhhÿ\u000e\u000e\u000eÿ\u0000\u0000\u0000ÿ\u0001\u0001\u0001ÿ¸¸¸ÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿþþÿÿ±ËâÿL\u0089¼ÿ9}µÿ:}µÿ:}µÿ:}µÿ:}µÿ:}µÿM\u0089¼ÿ¿Ôçÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿìììÿ\u0093\u0093\u0093ÿ\u009b\u009b\u009bÿýýýÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿó÷úÿµÎãÿ\u0081¬Ðÿe\u0099Åÿa\u0097Äÿl\u009eÈÿ\u008fµÕÿÅÙéÿ÷úüÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿ";
   public static final ByteBuffer LWJGLIcon16x16 = loadIcon("ÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿþÿÿÿÂ×èÿt¤ËÿP\u008b½ÿT\u008e¿ÿ\u0086¯Òÿçïöÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿõõõÿ\u008d\u008f\u0091ÿv\u0082\u008dÿ}\u008d\u009bÿ\u0084\u0099ªÿ\u0094·Õÿ:}µÿH\u0086ºÿÚçñÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿôøûÿ\u009c\u009e ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿäääÿ\u0084\u00adÐÿ:}µÿ[\u0092Áÿüýþÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿ\u0091¶Õÿ___ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\"\"\"ÿÿÿÿÿèðöÿ9|µÿ:}µÿÄØéÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÆÙéÿ\u0081«Îÿ\u001d\u001d\u001dÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿfffÿÿÿÿÿÐàíÿ:}µÿ:}µÿ\u008d´Ôÿÿÿÿÿòòòÿ¥¥¥ÿßßßÿ¢ÁÜÿ°ÅÖÿ\n\n\nÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ¨¨¨ÿÿÿÿÿ\u0097ºØÿ:}µÿ:}µÿq¡Êÿÿÿÿÿ¡¡¡ÿ\u0000\u0000\u0000ÿ\u0001\u0001\u0001ÿ###ÿÌÌÌÿÐÐÐÿ¥¥¥ÿ\u0084\u0084\u0084ÿ\\\\\\ÿïïïÿÿÿÿÿ`\u0096Ãÿ:}µÿ:}µÿm\u009eÈÿÿÿÿÿ^^^ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿÏÏÏÿ\u001f\u001f\u001fÿ\u0003\u0003\u0003ÿ+++ÿlllÿÆÆÆÿúüýÿ\u009e¿Úÿw¥ÌÿL\u0089¼ÿ|¨Îÿÿÿÿÿ\u001b\u001b\u001bÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0017\u0017\u0017ÿÖÖÖÿ\u0001\u0001\u0001ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿRRRÿåååÿ===ÿhhhÿ¦¦§ÿÚÞáÿÿÿÿÿtttÿ\u000e\u000e\u000eÿ\u0000\u0000\u0000ÿYYYÿ\u0095\u0095\u0095ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0091\u0091\u0091ÿ\u009b\u009b\u009bÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0002\u0002\u0002ÿCCCÿÿÿÿÿîîîÿ\u008c\u008c\u008cÿ¿¿¿ÿVVVÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿÓÓÓÿXXXÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ333ÿÿÿÿÿÿÿÿÿüýþÿÿÿÿÿÀÀÀÿ@@@ÿ\u0002\u0002\u0002ÿ\u0000\u0000\u0000ÿ\u001b\u001b\u001bÿûûûÿ\u0017\u0017\u0017ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿxxxÿÿÿÿÿÿÿÿÿÒáîÿ~©ÎÿàêóÿÿÿÿÿÔÔÔÿmmmÿ\u0084\u0084\u0084ÿÓÓÓÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ½½½ÿÿÿÿÿÿÿÿÿþþþÿf\u009aÅÿ=\u007f¶ÿ\u0082¬Ðÿäíõÿÿÿÿÿÿÿÿÿåååÿ---ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\f\f\fÿöööÿÿÿÿÿÿÿÿÿÿÿÿÿâìôÿM\u0089¼ÿ:}µÿ>\u0080¶ÿ\u0087°Òÿçïöÿÿÿÿÿþþþÿ¼¼¼ÿOOOÿ\u0003\u0003\u0003ÿJJJÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿëòøÿ\u008c³ÔÿV\u0090ÀÿP\u008c½ÿw¥Ìÿíóøÿÿÿÿÿÿÿÿÿÿÿÿÿßßßÿæææÿÿÿÿÿ");
   public static final ByteBuffer LWJGLIcon32x32 = loadIcon("ÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿúüýÿËÝìÿ\u0093·Öÿo Éÿ\\\u0093Âÿ`\u0096Ãÿz§Íÿ¬ÈàÿíóøÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿüýþÿµÎãÿZ\u0092Áÿ:}µÿ:}µÿ:}µÿ:}µÿ:}µÿ:}µÿ:}µÿE\u0085¹ÿ¡ÁÜÿüýþÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿüüüÿâêñÿ¸Ïãÿ®Éàÿ£ÂÜÿ\u0098ºØÿ\u008c³Óÿ\u0081¬Ïÿn\u009fÈÿ=\u007f¶ÿ:}µÿ:}µÿ:}µÿr¢ÊÿõøûÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿØØØÿ111ÿ$$$ÿ111ÿ@@@ÿRRRÿeeeÿtttÿ\u008f\u008f\u008fÿ÷øøÿ¬Èßÿ;}µÿ:}µÿ:}µÿ:}µÿy§Íÿüýþÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿþþþÿhhhÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ¶¶¶ÿþþþÿ\u0086®Ñÿ:}µÿ:}µÿ:}µÿ:}µÿ®ÊáÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÓâîÿáêòÿ(((ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0002\u0002\u0002ÿÞÞÞÿÿÿÿÿõøûÿ[\u0092Áÿ:}µÿ:}µÿ:}µÿH\u0086ºÿôøûÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿìóøÿd\u0099Åÿ×ÙÛÿ\u0005\u0005\u0005ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ$$$ÿþþþÿÿÿÿÿÿÿÿÿÀÕçÿ9|µÿ:}µÿ:}µÿ:}µÿ«Èàÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿþÿÿÿx¦Ìÿ{§Íÿ\u009e\u009e\u009eÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿeeeÿÿÿÿÿÿÿÿÿÿÿÿÿâìôÿ7{´ÿ:}µÿ:}µÿ:}µÿf\u009aÅÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÃ×èÿ9}µÿ²ÌáÿXXXÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ©©©ÿÿÿÿÿÿÿÿÿÿÿÿÿ¼Òåÿ:}µÿ:}µÿ:}µÿ:}µÿ@\u0081·ÿîôùÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿúüýÿZ\u0092Áÿ=\u007f¶ÿÝåíÿ\u001c\u001c\u001cÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0007\u0007\u0007ÿçççÿÿÿÿÿÿÿÿÿÿÿÿÿ\u0085®Ñÿ:}µÿ:}µÿ:}µÿ:}µÿ9}µÿÌÞìÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿþþþÿÿÿÿÿÿÿÿÿÿÿÿÿºÒåÿ:}µÿW\u0090¿ÿÐÑÑÿ\u0003\u0003\u0003ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ333ÿýýýÿÿÿÿÿÿÿÿÿûüýÿP\u008b½ÿ:}µÿ:}µÿ:}µÿ:}µÿ9}µÿ±ÌâÿÿÿÿÿÿÿÿÿÿÿÿÿÊÊÊÿ777ÿaaaÿ¢¢¢ÿÝÝÝÿäìóÿ±Ëáÿ´Íâÿæææÿ###ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿrrrÿÿÿÿÿÿÿÿÿÿÿÿÿØåðÿ:}µÿ:}µÿ:}µÿ:}µÿ:}µÿ:}µÿ ÀÛÿÿÿÿÿÿÿÿÿÿÿÿÿfffÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0006\u0006\u0006ÿ\'\'\'ÿfffÿÔÔÔÿÿÿÿÿöööÿÐÐÐÿ\u0097\u0097\u0097ÿ^^^ÿ\'\'\'ÿ\u0004\u0004\u0004ÿ\u0000\u0000\u0000ÿ\u0003\u0003\u0003ÿÀÀÀÿÿÿÿÿÿÿÿÿÿÿÿÿ¢ÁÛÿ:}µÿ:}µÿ:}µÿ:}µÿ:}µÿ:}µÿ\u009e¿Úÿÿÿÿÿÿÿÿÿûûûÿ%%%ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\\\\\\ÿÿÿÿÿêêêÿ\u008f\u008f\u008fÿµµµÿéééÿýýýÿçççÿ²²²ÿ½½½ÿþþþÿÿÿÿÿÿÿÿÿÿÿÿÿk\u009dÇÿ:}µÿ:}µÿ:}µÿ:}µÿ:}µÿ:}µÿ¡ÁÜÿÿÿÿÿÿÿÿÿÛÛÛÿ\u0003\u0003\u0003ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0083\u0083\u0083ÿÿÿÿÿ___ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u000b\u000b\u000bÿ777ÿvvvÿ···ÿðððÿÿÿÿÿÿÿÿÿÿÿÿÿðõùÿK\u0088»ÿ=\u007f¶ÿ9|µÿ:}µÿ:}µÿ:}µÿ:}µÿ²Ìâÿÿÿÿÿÿÿÿÿ\u009a\u009a\u009aÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿÅÅÅÿ÷÷÷ÿ\u001d\u001d\u001dÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0007\u0007\u0007ÿ<<<ÿßßßÿÿÿÿÿûüýÿüýþÿó÷úÿÌÝëÿ\u009e¾Úÿr¡ÉÿL\u0089»ÿ:}µÿÉÜëÿÿÿÿÿÿÿÿÿVVVÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u000f\u000f\u000fÿùùùÿÑÑÑÿ\u0003\u0003\u0003ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u008e\u008e\u008eÿÿÿÿÿùùùÿ\u0084\u0084\u0084ÿpppÿ°°°ÿêêêÿÿÿÿÿùûüÿÜèòÿöùüÿÿÿÿÿÿÿÿÿ\u0015\u0015\u0015ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿNNNÿÿÿÿÿ\u0091\u0091\u0091ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ»»»ÿÿÿÿÿ\u009e\u009e\u009eÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0004\u0004\u0004ÿ///ÿpppÿ°°°ÿæææÿþþþÿÿÿÿÿ999ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0090\u0090\u0090ÿÿÿÿÿMMMÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0010\u0010\u0010ÿðððÿÿÿÿÿZZZÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\t\t\tÿ222ÿ±±±ÿöööÿ¡¡¡ÿ555ÿ\u0002\u0002\u0002ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0002\u0002\u0002ÿÑÑÑÿõõõÿ\u0013\u0013\u0013ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿEEEÿþþþÿøøøÿ\u001d\u001d\u001dÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ+++ÿÿÿÿÿÿÿÿÿüüüÿ¾¾¾ÿPPPÿ\u0005\u0005\u0005ÿ%%%ÿùùùÿÂÂÂÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0086\u0086\u0086ÿÿÿÿÿÒÒÒÿ\u0001\u0001\u0001ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿCCCÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿþþþÿÜÜÜÿàààÿÿÿÿÿ\u0095\u0095\u0095ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0001\u0001\u0001ÿÈÈÈÿÿÿÿÿ\u008e\u008e\u008eÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0088\u0088\u0088ÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿßßßÿ)))ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0018\u0018\u0018ÿõõõÿÿÿÿÿJJJÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿÏÏÏÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿñöúÿÿÿÿÿÿÿÿÿÿÿÿÿùùùÿµµµÿKKKÿ\u0006\u0006\u0006ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿRRRÿÿÿÿÿøøøÿ\u0010\u0010\u0010ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u001c\u001c\u001cÿøøøÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿ\u0086¯Ñÿ\u0090µÕÿêñ÷ÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÛÛÛÿxxxÿ\u001b\u001b\u001bÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0096\u0096\u0096ÿÿÿÿÿÆÆÆÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿZZZÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÃØéÿ:}µÿC\u0083¸ÿ\u0094¸Öÿíóøÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿôôôÿ§§§ÿ\u0085\u0085\u0085ÿôôôÿÿÿÿÿ\u0086\u0086\u0086ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u009d\u009d\u009dÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿúûýÿ]\u0094Âÿ:}µÿ:}µÿF\u0085¹ÿ\u0098»Øÿïôùÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿ\u009b\u009b\u009bÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0005\u0005\u0005ÿÝÝÝÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÅÙéÿ;~¶ÿ:}µÿ:}µÿ:}µÿH\u0086ºÿ\u009e¾Úÿòöúÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿûûûÿ\u008e\u008e\u008eÿ%%%ÿ\u0001\u0001\u0001ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ,,,ÿüüüÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿ\u008fµÕÿ:}µÿ:}µÿ:}µÿ:}µÿ:}µÿJ\u0087»ÿ£ÂÜÿó÷úÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿúúúÿ°°°ÿEEEÿ\u0004\u0004\u0004ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿ\u0000\u0000\u0000ÿnnnÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿüýþÿ\u0085¯Ñÿ:}µÿ:}µÿ:}µÿ:}µÿ:}µÿ:}µÿM\u0089¼ÿ¨ÅÞÿöùüÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿþþþÿÒÒÒÿhhhÿ\u000e\u000e\u000eÿ\u0000\u0000\u0000ÿ\u0001\u0001\u0001ÿ¸¸¸ÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿþþÿÿ±ËâÿL\u0089¼ÿ9}µÿ:}µÿ:}µÿ:}µÿ:}µÿ:}µÿM\u0089¼ÿ¿Ôçÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿìììÿ\u0093\u0093\u0093ÿ\u009b\u009b\u009bÿýýýÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿó÷úÿµÎãÿ\u0081¬Ðÿe\u0099Åÿa\u0097Äÿl\u009eÈÿ\u008fµÕÿÅÙéÿ÷úüÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿ");
   public static final boolean DEBUG = getPrivilegedBoolean("org.lwjgl.util.Debug");
   public static final boolean CHECKS = !getPrivilegedBoolean("org.lwjgl.util.NoChecks");
   private static final int PLATFORM;

   private static ByteBuffer loadIcon(String data) {
      int len = data.length();
      ByteBuffer bb = BufferUtils.createByteBuffer(len);

      for(int i = 0; i < len; ++i) {
         bb.put(i, (byte)data.charAt(i));
      }

      return bb.asReadOnlyBuffer();
   }

   public static int getPlatform() {
      return PLATFORM;
   }

   public static String getPlatformName() {
      switch(getPlatform()) {
      case 1:
         return "linux";
      case 2:
         return "macosx";
      case 3:
         return "windows";
      default:
         return "unknown";
      }
   }

   public static String mapLibraryName(String name) {
      String libName = System.mapLibraryName(name);
      return getPlatform() == 2 && libName.endsWith(".jnilib")?libName.substring(0, libName.length() - ".jnilib".length()) + ".dylib":libName;
   }

   public static String[] getLibraryPaths(String libname, String platform_lib_name, ClassLoader classloader) {
      return getLibraryPaths(libname, new String[]{platform_lib_name}, classloader);
   }

   public static String[] getLibraryPaths(String libname, String[] platform_lib_names, ClassLoader classloader) {
      List<String> possible_paths = new ArrayList();
      String classloader_path = getPathFromClassLoader(libname, classloader);
      if(classloader_path != null) {
         log("getPathFromClassLoader: Path found: " + classloader_path);
         possible_paths.add(classloader_path);
      }

      for(String platform_lib_name : platform_lib_names) {
         String lwjgl_classloader_path = getPathFromClassLoader("lwjgl", classloader);
         if(lwjgl_classloader_path != null) {
            log("getPathFromClassLoader: Path found: " + lwjgl_classloader_path);
            possible_paths.add(lwjgl_classloader_path.substring(0, lwjgl_classloader_path.lastIndexOf(File.separator)) + File.separator + platform_lib_name);
         }

         String alternative_path = getPrivilegedProperty("org.lwjgl.librarypath");
         if(alternative_path != null) {
            possible_paths.add(alternative_path + File.separator + platform_lib_name);
         }

         String java_library_path = getPrivilegedProperty("java.library.path");
         StringTokenizer st = new StringTokenizer(java_library_path, File.pathSeparator);

         while(st.hasMoreTokens()) {
            String path = st.nextToken();
            possible_paths.add(path + File.separator + platform_lib_name);
         }

         String current_dir = getPrivilegedProperty("user.dir");
         possible_paths.add(current_dir + File.separator + platform_lib_name);
         possible_paths.add(platform_lib_name);
      }

      return (String[])possible_paths.toArray(new String[possible_paths.size()]);
   }

   static void execPrivileged(final String[] cmd_array) throws Exception {
      try {
         Process process = (Process)AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Process run() throws Exception {
               return Runtime.getRuntime().exec(cmd_array);
            }
         });
         process.getInputStream().close();
         process.getOutputStream().close();
         process.getErrorStream().close();
      } catch (PrivilegedActionException var2) {
         throw (Exception)var2.getCause();
      }
   }

   private static String getPrivilegedProperty(final String property_name) {
      return (String)AccessController.doPrivileged(new PrivilegedAction() {
         public String run() {
            return System.getProperty(property_name);
         }
      });
   }

   private static String getPathFromClassLoader(final String libname, final ClassLoader classloader) {
      Class<?> c = null;

      try {
         log("getPathFromClassLoader: searching for: " + libname);

         for(c = classloader.getClass(); c != null; c = c.getSuperclass()) {
            final Class<?> clazz = c;

            try {
               return (String)AccessController.doPrivileged(new PrivilegedExceptionAction() {
                  public String run() throws Exception {
                     Method findLibrary = clazz.getDeclaredMethod("findLibrary", new Class[]{String.class});
                     findLibrary.setAccessible(true);
                     String path = (String)findLibrary.invoke(classloader, new Object[]{libname});
                     return path;
                  }
               });
            } catch (PrivilegedActionException var5) {
               log("Failed to locate findLibrary method: " + var5.getCause());
            }
         }
      } catch (Exception var6) {
         log("Failure locating " + var6 + " using classloader:" + c);
      }

      return null;
   }

   public static boolean getPrivilegedBoolean(final String property_name) {
      return ((Boolean)AccessController.doPrivileged(new PrivilegedAction() {
         public Boolean run() {
            return Boolean.valueOf(Boolean.getBoolean(property_name));
         }
      })).booleanValue();
   }

   public static Integer getPrivilegedInteger(final String property_name) {
      return (Integer)AccessController.doPrivileged(new PrivilegedAction() {
         public Integer run() {
            return Integer.getInteger(property_name);
         }
      });
   }

   public static Integer getPrivilegedInteger(final String property_name, final int default_val) {
      return (Integer)AccessController.doPrivileged(new PrivilegedAction() {
         public Integer run() {
            return Integer.getInteger(property_name, default_val);
         }
      });
   }

   public static void log(CharSequence msg) {
      if(DEBUG) {
         System.err.println("[LWJGL] " + msg);
      }

   }

   public static boolean isMacOSXEqualsOrBetterThan(int major_required, int minor_required) {
      String os_version = getPrivilegedProperty("os.version");
      StringTokenizer version_tokenizer = new StringTokenizer(os_version, ".");

      int major;
      int minor;
      try {
         String major_str = version_tokenizer.nextToken();
         String minor_str = version_tokenizer.nextToken();
         major = Integer.parseInt(major_str);
         minor = Integer.parseInt(minor_str);
      } catch (Exception var8) {
         log("Exception occurred while trying to determine OS version: " + var8);
         return false;
      }

      return major > major_required || major == major_required && minor >= minor_required;
   }

   public static Map getClassTokens(LWJGLUtil.TokenFilter filter, Map target, Class... tokenClasses) {
      return getClassTokens(filter, target, (Iterable)Arrays.asList(tokenClasses));
   }

   public static Map getClassTokens(LWJGLUtil.TokenFilter filter, Map target, Iterable tokenClasses) {
      if(target == null) {
         target = new HashMap();
      }

      int TOKEN_MODIFIERS = 25;

      for(Class tokenClass : tokenClasses) {
         for(Field field : tokenClass.getDeclaredFields()) {
            if((field.getModifiers() & 25) == 25 && field.getType() == Integer.TYPE) {
               try {
                  int value = field.getInt((Object)null);
                  if(filter == null || filter.accept(field, value)) {
                     if(((Map)target).containsKey(Integer.valueOf(value))) {
                        ((Map)target).put(Integer.valueOf(value), toHexString(value));
                     } else {
                        ((Map)target).put(Integer.valueOf(value), field.getName());
                     }
                  }
               } catch (IllegalAccessException var11) {
                  ;
               }
            }
         }
      }

      return (Map)target;
   }

   public static String toHexString(int value) {
      return "0x" + Integer.toHexString(value).toUpperCase();
   }

   static {
      String osName = getPrivilegedProperty("os.name");
      if(osName.startsWith("Windows")) {
         PLATFORM = 3;
      } else if(!osName.startsWith("Linux") && !osName.startsWith("FreeBSD") && !osName.startsWith("OpenBSD") && !osName.startsWith("SunOS") && !osName.startsWith("Unix")) {
         if(!osName.startsWith("Mac OS X") && !osName.startsWith("Darwin")) {
            throw new LinkageError("Unknown platform: " + osName);
         }

         PLATFORM = 2;
      } else {
         PLATFORM = 1;
      }

   }

   public interface TokenFilter {
      boolean accept(Field var1, int var2);
   }
}
