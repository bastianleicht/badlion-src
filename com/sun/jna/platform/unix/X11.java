package com.sun.jna.platform.unix;

import com.sun.jna.Callback;
import com.sun.jna.FromNativeContext;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;

public interface X11 extends Library {
   X11 INSTANCE = (X11)Native.loadLibrary("X11", X11.class);
   int XK_0 = 48;
   int XK_9 = 57;
   int XK_A = 65;
   int XK_Z = 90;
   int XK_a = 97;
   int XK_z = 122;
   int XK_Shift_L = 65505;
   int XK_Shift_R = 65505;
   int XK_Control_L = 65507;
   int XK_Control_R = 65508;
   int XK_CapsLock = 65509;
   int XK_ShiftLock = 65510;
   int XK_Meta_L = 65511;
   int XK_Meta_R = 65512;
   int XK_Alt_L = 65513;
   int XK_Alt_R = 65514;
   int VisualNoMask = 0;
   int VisualIDMask = 1;
   int VisualScreenMask = 2;
   int VisualDepthMask = 4;
   int VisualClassMask = 8;
   int VisualRedMaskMask = 16;
   int VisualGreenMaskMask = 32;
   int VisualBlueMaskMask = 64;
   int VisualColormapSizeMask = 128;
   int VisualBitsPerRGBMask = 256;
   int VisualAllMask = 511;
   X11.Atom XA_PRIMARY = new X11.Atom(1L);
   X11.Atom XA_SECONDARY = new X11.Atom(2L);
   X11.Atom XA_ARC = new X11.Atom(3L);
   X11.Atom XA_ATOM = new X11.Atom(4L);
   X11.Atom XA_BITMAP = new X11.Atom(5L);
   X11.Atom XA_CARDINAL = new X11.Atom(6L);
   X11.Atom XA_COLORMAP = new X11.Atom(7L);
   X11.Atom XA_CURSOR = new X11.Atom(8L);
   X11.Atom XA_CUT_BUFFER0 = new X11.Atom(9L);
   X11.Atom XA_CUT_BUFFER1 = new X11.Atom(10L);
   X11.Atom XA_CUT_BUFFER2 = new X11.Atom(11L);
   X11.Atom XA_CUT_BUFFER3 = new X11.Atom(12L);
   X11.Atom XA_CUT_BUFFER4 = new X11.Atom(13L);
   X11.Atom XA_CUT_BUFFER5 = new X11.Atom(14L);
   X11.Atom XA_CUT_BUFFER6 = new X11.Atom(15L);
   X11.Atom XA_CUT_BUFFER7 = new X11.Atom(16L);
   X11.Atom XA_DRAWABLE = new X11.Atom(17L);
   X11.Atom XA_FONT = new X11.Atom(18L);
   X11.Atom XA_INTEGER = new X11.Atom(19L);
   X11.Atom XA_PIXMAP = new X11.Atom(20L);
   X11.Atom XA_POINT = new X11.Atom(21L);
   X11.Atom XA_RECTANGLE = new X11.Atom(22L);
   X11.Atom XA_RESOURCE_MANAGER = new X11.Atom(23L);
   X11.Atom XA_RGB_COLOR_MAP = new X11.Atom(24L);
   X11.Atom XA_RGB_BEST_MAP = new X11.Atom(25L);
   X11.Atom XA_RGB_BLUE_MAP = new X11.Atom(26L);
   X11.Atom XA_RGB_DEFAULT_MAP = new X11.Atom(27L);
   X11.Atom XA_RGB_GRAY_MAP = new X11.Atom(28L);
   X11.Atom XA_RGB_GREEN_MAP = new X11.Atom(29L);
   X11.Atom XA_RGB_RED_MAP = new X11.Atom(30L);
   X11.Atom XA_STRING = new X11.Atom(31L);
   X11.Atom XA_VISUALID = new X11.Atom(32L);
   X11.Atom XA_WINDOW = new X11.Atom(33L);
   X11.Atom XA_WM_COMMAND = new X11.Atom(34L);
   X11.Atom XA_WM_HINTS = new X11.Atom(35L);
   X11.Atom XA_WM_CLIENT_MACHINE = new X11.Atom(36L);
   X11.Atom XA_WM_ICON_NAME = new X11.Atom(37L);
   X11.Atom XA_WM_ICON_SIZE = new X11.Atom(38L);
   X11.Atom XA_WM_NAME = new X11.Atom(39L);
   X11.Atom XA_WM_NORMAL_HINTS = new X11.Atom(40L);
   X11.Atom XA_WM_SIZE_HINTS = new X11.Atom(41L);
   X11.Atom XA_WM_ZOOM_HINTS = new X11.Atom(42L);
   X11.Atom XA_MIN_SPACE = new X11.Atom(43L);
   X11.Atom XA_NORM_SPACE = new X11.Atom(44L);
   X11.Atom XA_MAX_SPACE = new X11.Atom(45L);
   X11.Atom XA_END_SPACE = new X11.Atom(46L);
   X11.Atom XA_SUPERSCRIPT_X = new X11.Atom(47L);
   X11.Atom XA_SUPERSCRIPT_Y = new X11.Atom(48L);
   X11.Atom XA_SUBSCRIPT_X = new X11.Atom(49L);
   X11.Atom XA_SUBSCRIPT_Y = new X11.Atom(50L);
   X11.Atom XA_UNDERLINE_POSITION = new X11.Atom(51L);
   X11.Atom XA_UNDERLINE_THICKNESS = new X11.Atom(52L);
   X11.Atom XA_STRIKEOUT_ASCENT = new X11.Atom(53L);
   X11.Atom XA_STRIKEOUT_DESCENT = new X11.Atom(54L);
   X11.Atom XA_ITALIC_ANGLE = new X11.Atom(55L);
   X11.Atom XA_X_HEIGHT = new X11.Atom(56L);
   X11.Atom XA_QUAD_WIDTH = new X11.Atom(57L);
   X11.Atom XA_WEIGHT = new X11.Atom(58L);
   X11.Atom XA_POINT_SIZE = new X11.Atom(59L);
   X11.Atom XA_RESOLUTION = new X11.Atom(60L);
   X11.Atom XA_COPYRIGHT = new X11.Atom(61L);
   X11.Atom XA_NOTICE = new X11.Atom(62L);
   X11.Atom XA_FONT_NAME = new X11.Atom(63L);
   X11.Atom XA_FAMILY_NAME = new X11.Atom(64L);
   X11.Atom XA_FULL_NAME = new X11.Atom(65L);
   X11.Atom XA_CAP_HEIGHT = new X11.Atom(66L);
   X11.Atom XA_WM_CLASS = new X11.Atom(67L);
   X11.Atom XA_WM_TRANSIENT_FOR = new X11.Atom(68L);
   X11.Atom XA_LAST_PREDEFINED = XA_WM_TRANSIENT_FOR;
   int None = 0;
   int ParentRelative = 1;
   int CopyFromParent = 0;
   int PointerWindow = 0;
   int InputFocus = 1;
   int PointerRoot = 1;
   int AnyPropertyType = 0;
   int AnyKey = 0;
   int AnyButton = 0;
   int AllTemporary = 0;
   int CurrentTime = 0;
   int NoSymbol = 0;
   int NoEventMask = 0;
   int KeyPressMask = 1;
   int KeyReleaseMask = 2;
   int ButtonPressMask = 4;
   int ButtonReleaseMask = 8;
   int EnterWindowMask = 16;
   int LeaveWindowMask = 32;
   int PointerMotionMask = 64;
   int PointerMotionHintMask = 128;
   int Button1MotionMask = 256;
   int Button2MotionMask = 512;
   int Button3MotionMask = 1024;
   int Button4MotionMask = 2048;
   int Button5MotionMask = 4096;
   int ButtonMotionMask = 8192;
   int KeymapStateMask = 16384;
   int ExposureMask = 32768;
   int VisibilityChangeMask = 65536;
   int StructureNotifyMask = 131072;
   int ResizeRedirectMask = 262144;
   int SubstructureNotifyMask = 524288;
   int SubstructureRedirectMask = 1048576;
   int FocusChangeMask = 2097152;
   int PropertyChangeMask = 4194304;
   int ColormapChangeMask = 8388608;
   int OwnerGrabButtonMask = 16777216;
   int KeyPress = 2;
   int KeyRelease = 3;
   int ButtonPress = 4;
   int ButtonRelease = 5;
   int MotionNotify = 6;
   int EnterNotify = 7;
   int LeaveNotify = 8;
   int FocusIn = 9;
   int FocusOut = 10;
   int KeymapNotify = 11;
   int Expose = 12;
   int GraphicsExpose = 13;
   int NoExpose = 14;
   int VisibilityNotify = 15;
   int CreateNotify = 16;
   int DestroyNotify = 17;
   int UnmapNotify = 18;
   int MapNotify = 19;
   int MapRequest = 20;
   int ReparentNotify = 21;
   int ConfigureNotify = 22;
   int ConfigureRequest = 23;
   int GravityNotify = 24;
   int ResizeRequest = 25;
   int CirculateNotify = 26;
   int CirculateRequest = 27;
   int PropertyNotify = 28;
   int SelectionClear = 29;
   int SelectionRequest = 30;
   int SelectionNotify = 31;
   int ColormapNotify = 32;
   int ClientMessage = 33;
   int MappingNotify = 34;
   int LASTEvent = 35;
   int ShiftMask = 1;
   int LockMask = 2;
   int ControlMask = 4;
   int Mod1Mask = 8;
   int Mod2Mask = 16;
   int Mod3Mask = 32;
   int Mod4Mask = 64;
   int Mod5Mask = 128;
   int ShiftMapIndex = 0;
   int LockMapIndex = 1;
   int ControlMapIndex = 2;
   int Mod1MapIndex = 3;
   int Mod2MapIndex = 4;
   int Mod3MapIndex = 5;
   int Mod4MapIndex = 6;
   int Mod5MapIndex = 7;
   int Button1Mask = 256;
   int Button2Mask = 512;
   int Button3Mask = 1024;
   int Button4Mask = 2048;
   int Button5Mask = 4096;
   int AnyModifier = 32768;
   int Button1 = 1;
   int Button2 = 2;
   int Button3 = 3;
   int Button4 = 4;
   int Button5 = 5;
   int NotifyNormal = 0;
   int NotifyGrab = 1;
   int NotifyUngrab = 2;
   int NotifyWhileGrabbed = 3;
   int NotifyHint = 1;
   int NotifyAncestor = 0;
   int NotifyVirtual = 1;
   int NotifyInferior = 2;
   int NotifyNonlinear = 3;
   int NotifyNonlinearVirtual = 4;
   int NotifyPointer = 5;
   int NotifyPointerRoot = 6;
   int NotifyDetailNone = 7;
   int VisibilityUnobscured = 0;
   int VisibilityPartiallyObscured = 1;
   int VisibilityFullyObscured = 2;
   int PlaceOnTop = 0;
   int PlaceOnBottom = 1;
   int FamilyInternet = 0;
   int FamilyDECnet = 1;
   int FamilyChaos = 2;
   int FamilyInternet6 = 6;
   int FamilyServerInterpreted = 5;
   int PropertyNewValue = 0;
   int PropertyDelete = 1;
   int ColormapUninstalled = 0;
   int ColormapInstalled = 1;
   int GrabModeSync = 0;
   int GrabModeAsync = 1;
   int GrabSuccess = 0;
   int AlreadyGrabbed = 1;
   int GrabInvalidTime = 2;
   int GrabNotViewable = 3;
   int GrabFrozen = 4;
   int AsyncPointer = 0;
   int SyncPointer = 1;
   int ReplayPointer = 2;
   int AsyncKeyboard = 3;
   int SyncKeyboard = 4;
   int ReplayKeyboard = 5;
   int AsyncBoth = 6;
   int SyncBoth = 7;
   int RevertToNone = 0;
   int RevertToPointerRoot = 1;
   int RevertToParent = 2;
   int Success = 0;
   int BadRequest = 1;
   int BadValue = 2;
   int BadWindow = 3;
   int BadPixmap = 4;
   int BadAtom = 5;
   int BadCursor = 6;
   int BadFont = 7;
   int BadMatch = 8;
   int BadDrawable = 9;
   int BadAccess = 10;
   int BadAlloc = 11;
   int BadColor = 12;
   int BadGC = 13;
   int BadIDChoice = 14;
   int BadName = 15;
   int BadLength = 16;
   int BadImplementation = 17;
   int FirstExtensionError = 128;
   int LastExtensionError = 255;
   int InputOutput = 1;
   int InputOnly = 2;
   int CWBackPixmap = 1;
   int CWBackPixel = 2;
   int CWBorderPixmap = 4;
   int CWBorderPixel = 8;
   int CWBitGravity = 16;
   int CWWinGravity = 32;
   int CWBackingStore = 64;
   int CWBackingPlanes = 128;
   int CWBackingPixel = 256;
   int CWOverrideRedirect = 512;
   int CWSaveUnder = 1024;
   int CWEventMask = 2048;
   int CWDontPropagate = 4096;
   int CWColormap = 8192;
   int CWCursor = 16384;
   int CWX = 1;
   int CWY = 2;
   int CWWidth = 4;
   int CWHeight = 8;
   int CWBorderWidth = 16;
   int CWSibling = 32;
   int CWStackMode = 64;
   int ForgetGravity = 0;
   int NorthWestGravity = 1;
   int NorthGravity = 2;
   int NorthEastGravity = 3;
   int WestGravity = 4;
   int CenterGravity = 5;
   int EastGravity = 6;
   int SouthWestGravity = 7;
   int SouthGravity = 8;
   int SouthEastGravity = 9;
   int StaticGravity = 10;
   int UnmapGravity = 0;
   int NotUseful = 0;
   int WhenMapped = 1;
   int Always = 2;
   int IsUnmapped = 0;
   int IsUnviewable = 1;
   int IsViewable = 2;
   int SetModeInsert = 0;
   int SetModeDelete = 1;
   int DestroyAll = 0;
   int RetainPermanent = 1;
   int RetainTemporary = 2;
   int Above = 0;
   int Below = 1;
   int TopIf = 2;
   int BottomIf = 3;
   int Opposite = 4;
   int RaiseLowest = 0;
   int LowerHighest = 1;
   int PropModeReplace = 0;
   int PropModePrepend = 1;
   int PropModeAppend = 2;
   int GXclear = 0;
   int GXand = 1;
   int GXandReverse = 2;
   int GXcopy = 3;
   int GXandInverted = 4;
   int GXnoop = 5;
   int GXxor = 6;
   int GXor = 7;
   int GXnor = 8;
   int GXequiv = 9;
   int GXinvert = 10;
   int GXorReverse = 11;
   int GXcopyInverted = 12;
   int GXorInverted = 13;
   int GXnand = 14;
   int GXset = 15;
   int LineSolid = 0;
   int LineOnOffDash = 1;
   int LineDoubleDash = 2;
   int CapNotLast = 0;
   int CapButt = 1;
   int CapRound = 2;
   int CapProjecting = 3;
   int JoinMiter = 0;
   int JoinRound = 1;
   int JoinBevel = 2;
   int FillSolid = 0;
   int FillTiled = 1;
   int FillStippled = 2;
   int FillOpaqueStippled = 3;
   int EvenOddRule = 0;
   int WindingRule = 1;
   int ClipByChildren = 0;
   int IncludeInferiors = 1;
   int Unsorted = 0;
   int YSorted = 1;
   int YXSorted = 2;
   int YXBanded = 3;
   int CoordModeOrigin = 0;
   int CoordModePrevious = 1;
   int Complex = 0;
   int Nonconvex = 1;
   int Convex = 2;
   int ArcChord = 0;
   int ArcPieSlice = 1;
   int GCFunction = 1;
   int GCPlaneMask = 2;
   int GCForeground = 4;
   int GCBackground = 8;
   int GCLineWidth = 16;
   int GCLineStyle = 32;
   int GCCapStyle = 64;
   int GCJoinStyle = 128;
   int GCFillStyle = 256;
   int GCFillRule = 512;
   int GCTile = 1024;
   int GCStipple = 2048;
   int GCTileStipXOrigin = 4096;
   int GCTileStipYOrigin = 8192;
   int GCFont = 16384;
   int GCSubwindowMode = 32768;
   int GCGraphicsExposures = 65536;
   int GCClipXOrigin = 131072;
   int GCClipYOrigin = 262144;
   int GCClipMask = 524288;
   int GCDashOffset = 1048576;
   int GCDashList = 2097152;
   int GCArcMode = 4194304;
   int GCLastBit = 22;
   int FontLeftToRight = 0;
   int FontRightToLeft = 1;
   int FontChange = 255;
   int XYBitmap = 0;
   int XYPixmap = 1;
   int ZPixmap = 2;
   int AllocNone = 0;
   int AllocAll = 1;
   int DoRed = 1;
   int DoGreen = 2;
   int DoBlue = 4;
   int CursorShape = 0;
   int TileShape = 1;
   int StippleShape = 2;
   int AutoRepeatModeOff = 0;
   int AutoRepeatModeOn = 1;
   int AutoRepeatModeDefault = 2;
   int LedModeOff = 0;
   int LedModeOn = 1;
   int KBKeyClickPercent = 1;
   int KBBellPercent = 2;
   int KBBellPitch = 4;
   int KBBellDuration = 8;
   int KBLed = 16;
   int KBLedMode = 32;
   int KBKey = 64;
   int KBAutoRepeatMode = 128;
   int MappingSuccess = 0;
   int MappingBusy = 1;
   int MappingFailed = 2;
   int MappingModifier = 0;
   int MappingKeyboard = 1;
   int MappingPointer = 2;
   int DontPreferBlanking = 0;
   int PreferBlanking = 1;
   int DefaultBlanking = 2;
   int DisableScreenSaver = 0;
   int DisableScreenInterval = 0;
   int DontAllowExposures = 0;
   int AllowExposures = 1;
   int DefaultExposures = 2;
   int ScreenSaverReset = 0;
   int ScreenSaverActive = 1;
   int HostInsert = 0;
   int HostDelete = 1;
   int EnableAccess = 1;
   int DisableAccess = 0;
   int StaticGray = 0;
   int GrayScale = 1;
   int StaticColor = 2;
   int PseudoColor = 3;
   int TrueColor = 4;
   int DirectColor = 5;
   int LSBFirst = 0;
   int MSBFirst = 1;

   X11.Display XOpenDisplay(String var1);

   int XGetErrorText(X11.Display var1, int var2, byte[] var3, int var4);

   int XDefaultScreen(X11.Display var1);

   X11.Screen DefaultScreenOfDisplay(X11.Display var1);

   X11.Visual XDefaultVisual(X11.Display var1, int var2);

   X11.Colormap XDefaultColormap(X11.Display var1, int var2);

   int XDisplayWidth(X11.Display var1, int var2);

   int XDisplayHeight(X11.Display var1, int var2);

   X11.Window XDefaultRootWindow(X11.Display var1);

   X11.Window XRootWindow(X11.Display var1, int var2);

   int XAllocNamedColor(X11.Display var1, int var2, String var3, Pointer var4, Pointer var5);

   X11.XSizeHints XAllocSizeHints();

   void XSetWMProperties(X11.Display var1, X11.Window var2, String var3, String var4, String[] var5, int var6, X11.XSizeHints var7, Pointer var8, Pointer var9);

   int XFree(Pointer var1);

   X11.Window XCreateSimpleWindow(X11.Display var1, X11.Window var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9);

   X11.Pixmap XCreateBitmapFromData(X11.Display var1, X11.Window var2, Pointer var3, int var4, int var5);

   int XMapWindow(X11.Display var1, X11.Window var2);

   int XMapRaised(X11.Display var1, X11.Window var2);

   int XMapSubwindows(X11.Display var1, X11.Window var2);

   int XFlush(X11.Display var1);

   int XSync(X11.Display var1, boolean var2);

   int XEventsQueued(X11.Display var1, int var2);

   int XPending(X11.Display var1);

   int XUnmapWindow(X11.Display var1, X11.Window var2);

   int XDestroyWindow(X11.Display var1, X11.Window var2);

   int XCloseDisplay(X11.Display var1);

   int XClearWindow(X11.Display var1, X11.Window var2);

   int XClearArea(X11.Display var1, X11.Window var2, int var3, int var4, int var5, int var6, int var7);

   X11.Pixmap XCreatePixmap(X11.Display var1, X11.Drawable var2, int var3, int var4, int var5);

   int XFreePixmap(X11.Display var1, X11.Pixmap var2);

   X11.GC XCreateGC(X11.Display var1, X11.Drawable var2, NativeLong var3, X11.XGCValues var4);

   int XSetFillRule(X11.Display var1, X11.GC var2, int var3);

   int XFreeGC(X11.Display var1, X11.GC var2);

   int XDrawPoint(X11.Display var1, X11.Drawable var2, X11.GC var3, int var4, int var5);

   int XDrawPoints(X11.Display var1, X11.Drawable var2, X11.GC var3, X11.XPoint[] var4, int var5, int var6);

   int XFillRectangle(X11.Display var1, X11.Drawable var2, X11.GC var3, int var4, int var5, int var6, int var7);

   int XFillRectangles(X11.Display var1, X11.Drawable var2, X11.GC var3, X11.XRectangle[] var4, int var5);

   int XSetForeground(X11.Display var1, X11.GC var2, NativeLong var3);

   int XSetBackground(X11.Display var1, X11.GC var2, NativeLong var3);

   int XFillArc(X11.Display var1, X11.Drawable var2, X11.GC var3, int var4, int var5, int var6, int var7, int var8, int var9);

   int XFillPolygon(X11.Display var1, X11.Drawable var2, X11.GC var3, X11.XPoint[] var4, int var5, int var6, int var7);

   int XQueryTree(X11.Display var1, X11.Window var2, X11.WindowByReference var3, X11.WindowByReference var4, PointerByReference var5, IntByReference var6);

   boolean XQueryPointer(X11.Display var1, X11.Window var2, X11.WindowByReference var3, X11.WindowByReference var4, IntByReference var5, IntByReference var6, IntByReference var7, IntByReference var8, IntByReference var9);

   int XGetWindowAttributes(X11.Display var1, X11.Window var2, X11.XWindowAttributes var3);

   int XChangeWindowAttributes(X11.Display var1, X11.Window var2, NativeLong var3, X11.XSetWindowAttributes var4);

   int XGetGeometry(X11.Display var1, X11.Drawable var2, X11.WindowByReference var3, IntByReference var4, IntByReference var5, IntByReference var6, IntByReference var7, IntByReference var8, IntByReference var9);

   boolean XTranslateCoordinates(X11.Display var1, X11.Window var2, X11.Window var3, int var4, int var5, IntByReference var6, IntByReference var7, X11.WindowByReference var8);

   int XSelectInput(X11.Display var1, X11.Window var2, NativeLong var3);

   int XSendEvent(X11.Display var1, X11.Window var2, int var3, NativeLong var4, X11.XEvent var5);

   int XNextEvent(X11.Display var1, X11.XEvent var2);

   int XPeekEvent(X11.Display var1, X11.XEvent var2);

   int XWindowEvent(X11.Display var1, X11.Window var2, NativeLong var3, X11.XEvent var4);

   boolean XCheckWindowEvent(X11.Display var1, X11.Window var2, NativeLong var3, X11.XEvent var4);

   int XMaskEvent(X11.Display var1, NativeLong var2, X11.XEvent var3);

   boolean XCheckMaskEvent(X11.Display var1, NativeLong var2, X11.XEvent var3);

   boolean XCheckTypedEvent(X11.Display var1, int var2, X11.XEvent var3);

   boolean XCheckTypedWindowEvent(X11.Display var1, X11.Window var2, int var3, X11.XEvent var4);

   X11.XWMHints XGetWMHints(X11.Display var1, X11.Window var2);

   int XGetWMName(X11.Display var1, X11.Window var2, X11.XTextProperty var3);

   X11.XVisualInfo XGetVisualInfo(X11.Display var1, NativeLong var2, X11.XVisualInfo var3, IntByReference var4);

   X11.Colormap XCreateColormap(X11.Display var1, X11.Window var2, X11.Visual var3, int var4);

   int XGetWindowProperty(X11.Display var1, X11.Window var2, X11.Atom var3, NativeLong var4, NativeLong var5, boolean var6, X11.Atom var7, X11.AtomByReference var8, IntByReference var9, NativeLongByReference var10, NativeLongByReference var11, PointerByReference var12);

   int XChangeProperty(X11.Display var1, X11.Window var2, X11.Atom var3, X11.Atom var4, int var5, int var6, Pointer var7, int var8);

   int XDeleteProperty(X11.Display var1, X11.Window var2, X11.Atom var3);

   X11.Atom XInternAtom(X11.Display var1, String var2, boolean var3);

   String XGetAtomName(X11.Display var1, X11.Atom var2);

   int XCopyArea(X11.Display var1, X11.Drawable var2, X11.Drawable var3, X11.GC var4, int var5, int var6, int var7, int var8, int var9, int var10);

   X11.XImage XCreateImage(X11.Display var1, X11.Visual var2, int var3, int var4, int var5, Pointer var6, int var7, int var8, int var9, int var10);

   int XPutImage(X11.Display var1, X11.Drawable var2, X11.GC var3, X11.XImage var4, int var5, int var6, int var7, int var8, int var9, int var10);

   int XDestroyImage(X11.XImage var1);

   X11.XErrorHandler XSetErrorHandler(X11.XErrorHandler var1);

   String XKeysymToString(X11.KeySym var1);

   X11.KeySym XStringToKeysym(String var1);

   byte XKeysymToKeycode(X11.Display var1, X11.KeySym var2);

   X11.KeySym XKeycodeToKeysym(X11.Display var1, byte var2, int var3);

   int XGrabKey(X11.Display var1, int var2, int var3, X11.Window var4, int var5, int var6, int var7);

   int XUngrabKey(X11.Display var1, int var2, int var3, X11.Window var4);

   int XChangeKeyboardMapping(X11.Display var1, int var2, int var3, X11.KeySym[] var4, int var5);

   X11.KeySym XGetKeyboardMapping(X11.Display var1, byte var2, int var3, IntByReference var4);

   int XDisplayKeycodes(X11.Display var1, IntByReference var2, IntByReference var3);

   int XSetModifierMapping(X11.Display var1, X11.XModifierKeymapRef var2);

   X11.XModifierKeymapRef XGetModifierMapping(X11.Display var1);

   X11.XModifierKeymapRef XNewModifiermap(int var1);

   X11.XModifierKeymapRef XInsertModifiermapEntry(X11.XModifierKeymapRef var1, byte var2, int var3);

   X11.XModifierKeymapRef XDeleteModifiermapEntry(X11.XModifierKeymapRef var1, byte var2, int var3);

   int XFreeModifiermap(X11.XModifierKeymapRef var1);

   int XChangeKeyboardControl(X11.Display var1, NativeLong var2, X11.XKeyboardControlRef var3);

   int XGetKeyboardControl(X11.Display var1, X11.XKeyboardStateRef var2);

   int XAutoRepeatOn(X11.Display var1);

   int XAutoRepeatOff(X11.Display var1);

   int XBell(X11.Display var1, int var2);

   int XQueryKeymap(X11.Display var1, byte[] var2);

   public static class Atom extends X11.XID {
      private static final long serialVersionUID = 1L;
      public static final X11.Atom None = null;

      public Atom() {
      }

      public Atom(long id) {
         super(id);
      }

      public Object fromNative(Object nativeValue, FromNativeContext context) {
         long value = ((Number)nativeValue).longValue();
         if(value <= 2147483647L) {
            switch((int)value) {
            case 0:
               return None;
            case 1:
               return X11.XA_PRIMARY;
            case 2:
               return X11.XA_SECONDARY;
            case 3:
               return X11.XA_ARC;
            case 4:
               return X11.XA_ATOM;
            case 5:
               return X11.XA_BITMAP;
            case 6:
               return X11.XA_CARDINAL;
            case 7:
               return X11.XA_COLORMAP;
            case 8:
               return X11.XA_CURSOR;
            case 9:
               return X11.XA_CUT_BUFFER0;
            case 10:
               return X11.XA_CUT_BUFFER1;
            case 11:
               return X11.XA_CUT_BUFFER2;
            case 12:
               return X11.XA_CUT_BUFFER3;
            case 13:
               return X11.XA_CUT_BUFFER4;
            case 14:
               return X11.XA_CUT_BUFFER5;
            case 15:
               return X11.XA_CUT_BUFFER6;
            case 16:
               return X11.XA_CUT_BUFFER7;
            case 17:
               return X11.XA_DRAWABLE;
            case 18:
               return X11.XA_FONT;
            case 19:
               return X11.XA_INTEGER;
            case 20:
               return X11.XA_PIXMAP;
            case 21:
               return X11.XA_POINT;
            case 22:
               return X11.XA_RECTANGLE;
            case 23:
               return X11.XA_RESOURCE_MANAGER;
            case 24:
               return X11.XA_RGB_COLOR_MAP;
            case 25:
               return X11.XA_RGB_BEST_MAP;
            case 26:
               return X11.XA_RGB_BLUE_MAP;
            case 27:
               return X11.XA_RGB_DEFAULT_MAP;
            case 28:
               return X11.XA_RGB_GRAY_MAP;
            case 29:
               return X11.XA_RGB_GREEN_MAP;
            case 30:
               return X11.XA_RGB_RED_MAP;
            case 31:
               return X11.XA_STRING;
            case 32:
               return X11.XA_VISUALID;
            case 33:
               return X11.XA_WINDOW;
            case 34:
               return X11.XA_WM_COMMAND;
            case 35:
               return X11.XA_WM_HINTS;
            case 36:
               return X11.XA_WM_CLIENT_MACHINE;
            case 37:
               return X11.XA_WM_ICON_NAME;
            case 38:
               return X11.XA_WM_ICON_SIZE;
            case 39:
               return X11.XA_WM_NAME;
            case 40:
               return X11.XA_WM_NORMAL_HINTS;
            case 41:
               return X11.XA_WM_SIZE_HINTS;
            case 42:
               return X11.XA_WM_ZOOM_HINTS;
            case 43:
               return X11.XA_MIN_SPACE;
            case 44:
               return X11.XA_NORM_SPACE;
            case 45:
               return X11.XA_MAX_SPACE;
            case 46:
               return X11.XA_END_SPACE;
            case 47:
               return X11.XA_SUPERSCRIPT_X;
            case 48:
               return X11.XA_SUPERSCRIPT_Y;
            case 49:
               return X11.XA_SUBSCRIPT_X;
            case 50:
               return X11.XA_SUBSCRIPT_Y;
            case 51:
               return X11.XA_UNDERLINE_POSITION;
            case 52:
               return X11.XA_UNDERLINE_THICKNESS;
            case 53:
               return X11.XA_STRIKEOUT_ASCENT;
            case 54:
               return X11.XA_STRIKEOUT_DESCENT;
            case 55:
               return X11.XA_ITALIC_ANGLE;
            case 56:
               return X11.XA_X_HEIGHT;
            case 57:
               return X11.XA_QUAD_WIDTH;
            case 58:
               return X11.XA_WEIGHT;
            case 59:
               return X11.XA_POINT_SIZE;
            case 60:
               return X11.XA_RESOLUTION;
            case 61:
               return X11.XA_COPYRIGHT;
            case 62:
               return X11.XA_NOTICE;
            case 63:
               return X11.XA_FONT_NAME;
            case 64:
               return X11.XA_FAMILY_NAME;
            case 65:
               return X11.XA_FULL_NAME;
            case 66:
               return X11.XA_CAP_HEIGHT;
            case 67:
               return X11.XA_WM_CLASS;
            case 68:
               return X11.XA_WM_TRANSIENT_FOR;
            }
         }

         return new X11.Atom(value);
      }
   }

   public static class AtomByReference extends ByReference {
      public AtomByReference() {
         super(X11.XID.SIZE);
      }

      public X11.Atom getValue() {
         NativeLong value = this.getPointer().getNativeLong(0L);
         return (X11.Atom)(new X11.Atom()).fromNative(value, (FromNativeContext)null);
      }
   }

   public static class Colormap extends X11.XID {
      private static final long serialVersionUID = 1L;
      public static final X11.Colormap None = null;

      public Colormap() {
      }

      public Colormap(long id) {
         super(id);
      }

      public Object fromNative(Object nativeValue, FromNativeContext context) {
         return this.isNone(nativeValue)?None:new X11.Colormap(((Number)nativeValue).longValue());
      }
   }

   public static class Cursor extends X11.XID {
      private static final long serialVersionUID = 1L;
      public static final X11.Cursor None = null;

      public Cursor() {
      }

      public Cursor(long id) {
         super(id);
      }

      public Object fromNative(Object nativeValue, FromNativeContext context) {
         return this.isNone(nativeValue)?None:new X11.Cursor(((Number)nativeValue).longValue());
      }
   }

   public static class Display extends PointerType {
   }

   public static class Drawable extends X11.XID {
      private static final long serialVersionUID = 1L;
      public static final X11.Drawable None = null;

      public Drawable() {
      }

      public Drawable(long id) {
         super(id);
      }

      public Object fromNative(Object nativeValue, FromNativeContext context) {
         return this.isNone(nativeValue)?None:new X11.Drawable(((Number)nativeValue).longValue());
      }
   }

   public static class Font extends X11.XID {
      private static final long serialVersionUID = 1L;
      public static final X11.Font None = null;

      public Font() {
      }

      public Font(long id) {
         super(id);
      }

      public Object fromNative(Object nativeValue, FromNativeContext context) {
         return this.isNone(nativeValue)?None:new X11.Font(((Number)nativeValue).longValue());
      }
   }

   public static class GC extends PointerType {
   }

   public static class KeySym extends X11.XID {
      private static final long serialVersionUID = 1L;
      public static final X11.KeySym None = null;

      public KeySym() {
      }

      public KeySym(long id) {
         super(id);
      }

      public Object fromNative(Object nativeValue, FromNativeContext context) {
         return this.isNone(nativeValue)?None:new X11.KeySym(((Number)nativeValue).longValue());
      }
   }

   public static class Pixmap extends X11.Drawable {
      private static final long serialVersionUID = 1L;
      public static final X11.Pixmap None = null;

      public Pixmap() {
      }

      public Pixmap(long id) {
         super(id);
      }

      public Object fromNative(Object nativeValue, FromNativeContext context) {
         return this.isNone(nativeValue)?None:new X11.Pixmap(((Number)nativeValue).longValue());
      }
   }

   public static class Screen extends PointerType {
   }

   public static class Visual extends PointerType {
      public NativeLong getVisualID() {
         return this.getPointer() != null?this.getPointer().getNativeLong((long)Native.POINTER_SIZE):new NativeLong(0L);
      }

      public String toString() {
         return "Visual: VisualID=0x" + Long.toHexString(this.getVisualID().longValue());
      }
   }

   public static class VisualID extends NativeLong {
      private static final long serialVersionUID = 1L;

      public VisualID() {
      }

      public VisualID(long value) {
         super(value);
      }
   }

   public static class Window extends X11.Drawable {
      private static final long serialVersionUID = 1L;
      public static final X11.Window None = null;

      public Window() {
      }

      public Window(long id) {
         super(id);
      }

      public Object fromNative(Object nativeValue, FromNativeContext context) {
         return this.isNone(nativeValue)?None:new X11.Window(((Number)nativeValue).longValue());
      }
   }

   public static class WindowByReference extends ByReference {
      public WindowByReference() {
         super(X11.XID.SIZE);
      }

      public X11.Window getValue() {
         NativeLong value = this.getPointer().getNativeLong(0L);
         return value.longValue() == 0L?X11.Window.None:new X11.Window(value.longValue());
      }
   }

   public static class XAnyEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window window;
   }

   public static class XButtonEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window window;
      public X11.Window root;
      public X11.Window subwindow;
      public NativeLong time;
      public int x;
      public int y;
      public int x_root;
      public int y_root;
      public int state;
      public int button;
      public int same_screen;
   }

   public static class XButtonPressedEvent extends X11.XButtonEvent {
   }

   public static class XButtonReleasedEvent extends X11.XButtonEvent {
   }

   public static class XCirculateEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window event;
      public X11.Window window;
      public int place;
   }

   public static class XCirculateRequestEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window parent;
      public X11.Window window;
      public int place;
   }

   public static class XClientMessageEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window window;
      public X11.Atom message_type;
      public int format;
      public X11.XClientMessageEvent.Data data;

      public static class Data extends Union {
         public byte[] b = new byte[20];
         public short[] s = new short[10];
         public NativeLong[] l = new NativeLong[5];
      }
   }

   public static class XColormapEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window window;
      public X11.Colormap colormap;
      public int c_new;
      public int state;
   }

   public static class XConfigureEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window event;
      public X11.Window window;
      public int x;
      public int y;
      public int width;
      public int height;
      public int border_width;
      public X11.Window above;
      public int override_redirect;
   }

   public static class XConfigureRequestEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window parent;
      public X11.Window window;
      public int x;
      public int y;
      public int width;
      public int height;
      public int border_width;
      public X11.Window above;
      public int detail;
      public NativeLong value_mask;
   }

   public static class XCreateWindowEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window parent;
      public X11.Window window;
      public int x;
      public int y;
      public int width;
      public int height;
      public int border_width;
      public int override_redirect;
   }

   public static class XCrossingEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window window;
      public X11.Window root;
      public X11.Window subwindow;
      public NativeLong time;
      public int x;
      public int y;
      public int x_root;
      public int y_root;
      public int mode;
      public int detail;
      public int same_screen;
      public int focus;
      public int state;
   }

   public static class XDestroyWindowEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window event;
      public X11.Window window;
   }

   public static class XDeviceByReference extends Structure implements Structure.ByReference {
      public X11.XID device_id;
      public int num_classes;
      public X11.XInputClassInfoByReference classes;
   }

   public static class XEnterWindowEvent extends X11.XCrossingEvent {
   }

   public static class XErrorEvent extends Structure {
      public int type;
      public X11.Display display;
      public X11.XID resourceid;
      public NativeLong serial;
      public byte error_code;
      public byte request_code;
      public byte minor_code;
   }

   public interface XErrorHandler extends Callback {
      int apply(X11.Display var1, X11.XErrorEvent var2);
   }

   public static class XEvent extends Union {
      public int type;
      public X11.XAnyEvent xany;
      public X11.XKeyEvent xkey;
      public X11.XButtonEvent xbutton;
      public X11.XMotionEvent xmotion;
      public X11.XCrossingEvent xcrossing;
      public X11.XFocusChangeEvent xfocus;
      public X11.XExposeEvent xexpose;
      public X11.XGraphicsExposeEvent xgraphicsexpose;
      public X11.XNoExposeEvent xnoexpose;
      public X11.XVisibilityEvent xvisibility;
      public X11.XCreateWindowEvent xcreatewindow;
      public X11.XDestroyWindowEvent xdestroywindow;
      public X11.XUnmapEvent xunmap;
      public X11.XMapEvent xmap;
      public X11.XMapRequestEvent xmaprequest;
      public X11.XReparentEvent xreparent;
      public X11.XConfigureEvent xconfigure;
      public X11.XGravityEvent xgravity;
      public X11.XResizeRequestEvent xresizerequest;
      public X11.XConfigureRequestEvent xconfigurerequest;
      public X11.XCirculateEvent xcirculate;
      public X11.XCirculateRequestEvent xcirculaterequest;
      public X11.XPropertyEvent xproperty;
      public X11.XSelectionClearEvent xselectionclear;
      public X11.XSelectionRequestEvent xselectionrequest;
      public X11.XSelectionEvent xselection;
      public X11.XColormapEvent xcolormap;
      public X11.XClientMessageEvent xclient;
      public X11.XMappingEvent xmapping;
      public X11.XErrorEvent xerror;
      public X11.XKeymapEvent xkeymap;
      public NativeLong[] pad = new NativeLong[24];
   }

   public static class XExposeEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window window;
      public int x;
      public int y;
      public int width;
      public int height;
      public int count;
   }

   public static class XFocusChangeEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window window;
      public int mode;
      public int detail;
   }

   public static class XFocusInEvent extends X11.XFocusChangeEvent {
   }

   public static class XFocusOutEvent extends X11.XFocusChangeEvent {
   }

   public static class XGCValues extends Structure {
      public int function;
      public NativeLong plane_mask;
      public NativeLong foreground;
      public NativeLong background;
      public int line_width;
      public int line_style;
      public int cap_style;
      public int join_style;
      public int fill_style;
      public int fill_rule;
      public int arc_mode;
      public X11.Pixmap tile;
      public X11.Pixmap stipple;
      public int ts_x_origin;
      public int ts_y_origin;
      public X11.Font font;
      public int subwindow_mode;
      public boolean graphics_exposures;
      public int clip_x_origin;
      public int clip_y_origin;
      public X11.Pixmap clip_mask;
      public int dash_offset;
      public byte dashes;
   }

   public static class XGraphicsExposeEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Drawable drawable;
      public int x;
      public int y;
      public int width;
      public int height;
      public int count;
      public int major_code;
      public int minor_code;
   }

   public static class XGravityEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window event;
      public X11.Window window;
      public int x;
      public int y;
   }

   public static class XID extends NativeLong {
      private static final long serialVersionUID = 1L;
      public static final X11.XID None = null;

      public XID() {
         this(0L);
      }

      public XID(long id) {
         super(id);
      }

      protected boolean isNone(Object o) {
         return o == null || o instanceof Number && ((Number)o).longValue() == 0L;
      }

      public Object fromNative(Object nativeValue, FromNativeContext context) {
         return this.isNone(nativeValue)?None:new X11.XID(((Number)nativeValue).longValue());
      }

      public String toString() {
         return "0x" + Long.toHexString(this.longValue());
      }
   }

   public static class XImage extends PointerType {
   }

   public static class XInputClassInfoByReference extends Structure implements Structure.ByReference {
      public byte input_class;
      public byte event_type_base;
   }

   public static class XKeyEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window window;
      public X11.Window root;
      public X11.Window subwindow;
      public NativeLong time;
      public int x;
      public int y;
      public int x_root;
      public int y_root;
      public int state;
      public int keycode;
      public int same_screen;
   }

   public static class XKeyboardControlRef extends Structure implements Structure.ByReference {
      public int key_click_percent;
      public int bell_percent;
      public int bell_pitch;
      public int bell_duration;
      public int led;
      public int led_mode;
      public int key;
      public int auto_repeat_mode;

      public String toString() {
         return "XKeyboardControlByReference{key_click_percent=" + this.key_click_percent + ", bell_percent=" + this.bell_percent + ", bell_pitch=" + this.bell_pitch + ", bell_duration=" + this.bell_duration + ", led=" + this.led + ", led_mode=" + this.led_mode + ", key=" + this.key + ", auto_repeat_mode=" + this.auto_repeat_mode + '}';
      }
   }

   public static class XKeyboardStateRef extends Structure implements Structure.ByReference {
      public int key_click_percent;
      public int bell_percent;
      public int bell_pitch;
      public int bell_duration;
      public NativeLong led_mask;
      public int global_auto_repeat;
      public byte[] auto_repeats = new byte[32];

      public String toString() {
         return "XKeyboardStateByReference{key_click_percent=" + this.key_click_percent + ", bell_percent=" + this.bell_percent + ", bell_pitch=" + this.bell_pitch + ", bell_duration=" + this.bell_duration + ", led_mask=" + this.led_mask + ", global_auto_repeat=" + this.global_auto_repeat + ", auto_repeats=" + this.auto_repeats + '}';
      }
   }

   public static class XKeymapEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window window;
      public byte[] key_vector = new byte[32];
   }

   public static class XLeaveWindowEvent extends X11.XCrossingEvent {
   }

   public static class XMapEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window event;
      public X11.Window window;
      public int override_redirect;
   }

   public static class XMapRequestEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window parent;
      public X11.Window window;
   }

   public static class XMappingEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window window;
      public int request;
      public int first_keycode;
      public int count;
   }

   public static class XModifierKeymapRef extends Structure implements Structure.ByReference {
      public int max_keypermod;
      public Pointer modifiermap;
   }

   public static class XMotionEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window window;
      public X11.Window root;
      public X11.Window subwindow;
      public NativeLong time;
      public int x;
      public int y;
      public int x_root;
      public int y_root;
      public int state;
      public byte is_hint;
      public int same_screen;
   }

   public static class XNoExposeEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Drawable drawable;
      public int major_code;
      public int minor_code;
   }

   public static class XPoint extends Structure {
      public short x;
      public short y;

      public XPoint() {
      }

      public XPoint(short x, short y) {
         this.x = x;
         this.y = y;
      }
   }

   public static class XPointerMovedEvent extends X11.XMotionEvent {
   }

   public static class XPropertyEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window window;
      public X11.Atom atom;
      public NativeLong time;
      public int state;
   }

   public static class XRectangle extends Structure {
      public short x;
      public short y;
      public short width;
      public short height;

      public XRectangle() {
      }

      public XRectangle(short x, short y, short width, short height) {
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
      }
   }

   public static class XReparentEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window event;
      public X11.Window window;
      public X11.Window parent;
      public int x;
      public int y;
      public int override_redirect;
   }

   public static class XResizeRequestEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window window;
      public int width;
      public int height;
   }

   public static class XSelectionClearEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window window;
      public X11.Atom selection;
      public NativeLong time;
   }

   public static class XSelectionEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window requestor;
      public X11.Atom selection;
      public X11.Atom target;
      public X11.Atom property;
      public NativeLong time;
   }

   public static class XSelectionRequestEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window owner;
      public X11.Window requestor;
      public X11.Atom selection;
      public X11.Atom target;
      X11.Atom property;
      public NativeLong time;
   }

   public static class XSetWindowAttributes extends Structure {
      public X11.Pixmap background_pixmap;
      public NativeLong background_pixel;
      public X11.Pixmap border_pixmap;
      public NativeLong border_pixel;
      public int bit_gravity;
      public int win_gravity;
      public int backing_store;
      public NativeLong backing_planes;
      public NativeLong backing_pixel;
      public boolean save_under;
      public NativeLong event_mask;
      public NativeLong do_not_propagate_mask;
      public boolean override_redirect;
      public X11.Colormap colormap;
      public X11.Cursor cursor;
   }

   public static class XSizeHints extends Structure {
      public NativeLong flags;
      public int x;
      public int y;
      public int width;
      public int height;
      public int min_width;
      public int min_height;
      public int max_width;
      public int max_height;
      public int width_inc;
      public int height_inc;
      public X11.XSizeHints.Aspect min_aspect;
      public X11.XSizeHints.Aspect max_aspect;
      public int base_width;
      public int base_height;
      public int win_gravity;

      public static class Aspect extends Structure {
         public int x;
         public int y;
      }
   }

   public interface XTest extends Library {
      X11.XTest INSTANCE = (X11.XTest)Native.loadLibrary("Xtst", X11.XTest.class);

      boolean XTestQueryExtension(X11.Display var1, IntByReference var2, IntByReference var3, IntByReference var4, IntByReference var5);

      boolean XTestCompareCursorWithWindow(X11.Display var1, X11.Window var2, X11.Cursor var3);

      boolean XTestCompareCurrentCursorWithWindow(X11.Display var1, X11.Window var2);

      int XTestFakeKeyEvent(X11.Display var1, int var2, boolean var3, NativeLong var4);

      int XTestFakeButtonEvent(X11.Display var1, int var2, boolean var3, NativeLong var4);

      int XTestFakeMotionEvent(X11.Display var1, int var2, int var3, int var4, NativeLong var5);

      int XTestFakeRelativeMotionEvent(X11.Display var1, int var2, int var3, NativeLong var4);

      int XTestFakeDeviceKeyEvent(X11.Display var1, X11.XDeviceByReference var2, int var3, boolean var4, IntByReference var5, int var6, NativeLong var7);

      int XTestFakeDeviceButtonEvent(X11.Display var1, X11.XDeviceByReference var2, int var3, boolean var4, IntByReference var5, int var6, NativeLong var7);

      int XTestFakeProximityEvent(X11.Display var1, X11.XDeviceByReference var2, boolean var3, IntByReference var4, int var5, NativeLong var6);

      int XTestFakeDeviceMotionEvent(X11.Display var1, X11.XDeviceByReference var2, boolean var3, int var4, IntByReference var5, int var6, NativeLong var7);

      int XTestGrabControl(X11.Display var1, boolean var2);

      void XTestSetVisualIDOfVisual(X11.Visual var1, X11.VisualID var2);

      int XTestDiscard(X11.Display var1);
   }

   public static class XTextProperty extends Structure {
      public String value;
      public X11.Atom encoding;
      public int format;
      public NativeLong nitems;
   }

   public static class XUnmapEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window event;
      public X11.Window window;
      public int from_configure;
   }

   public static class XVisibilityEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public X11.Display display;
      public X11.Window window;
      public int state;
   }

   public static class XVisualInfo extends Structure {
      public X11.Visual visual;
      public X11.VisualID visualid;
      public int screen;
      public int depth;
      public int c_class;
      public NativeLong red_mask;
      public NativeLong green_mask;
      public NativeLong blue_mask;
      public int colormap_size;
      public int bits_per_rgb;
   }

   public static class XWMHints extends Structure {
      public NativeLong flags;
      public boolean input;
      public int initial_state;
      public X11.Pixmap icon_pixmap;
      public X11.Window icon_window;
      public int icon_x;
      public int icon_y;
      public X11.Pixmap icon_mask;
      public X11.XID window_group;
   }

   public static class XWindowAttributes extends Structure {
      public int x;
      public int y;
      public int width;
      public int height;
      public int border_width;
      public int depth;
      public X11.Visual visual;
      public X11.Window root;
      public int c_class;
      public int bit_gravity;
      public int win_gravity;
      public int backing_store;
      public NativeLong backing_planes;
      public NativeLong backing_pixel;
      public boolean save_under;
      public X11.Colormap colormap;
      public boolean map_installed;
      public int map_state;
      public NativeLong all_event_masks;
      public NativeLong your_event_mask;
      public NativeLong do_not_propagate_mask;
      public boolean override_redirect;
      public X11.Screen screen;
   }

   public interface Xevie extends Library {
      X11.Xevie INSTANCE = (X11.Xevie)Native.loadLibrary("Xevie", X11.Xevie.class);
      int XEVIE_UNMODIFIED = 0;
      int XEVIE_MODIFIED = 1;

      boolean XevieQueryVersion(X11.Display var1, IntByReference var2, IntByReference var3);

      int XevieStart(X11.Display var1);

      int XevieEnd(X11.Display var1);

      int XevieSendEvent(X11.Display var1, X11.XEvent var2, int var3);

      int XevieSelectInput(X11.Display var1, NativeLong var2);
   }

   public interface Xext extends Library {
      X11.Xext INSTANCE = (X11.Xext)Native.loadLibrary("Xext", X11.Xext.class);
      int ShapeBounding = 0;
      int ShapeClip = 1;
      int ShapeInput = 2;
      int ShapeSet = 0;
      int ShapeUnion = 1;
      int ShapeIntersect = 2;
      int ShapeSubtract = 3;
      int ShapeInvert = 4;

      void XShapeCombineMask(X11.Display var1, X11.Window var2, int var3, int var4, int var5, X11.Pixmap var6, int var7);
   }

   public interface Xrender extends Library {
      X11.Xrender INSTANCE = (X11.Xrender)Native.loadLibrary("Xrender", X11.Xrender.class);
      int PictTypeIndexed = 0;
      int PictTypeDirect = 1;

      X11.Xrender.XRenderPictFormat XRenderFindVisualFormat(X11.Display var1, X11.Visual var2);

      public static class PictFormat extends NativeLong {
         private static final long serialVersionUID = 1L;

         public PictFormat(long value) {
            super(value);
         }

         public PictFormat() {
         }
      }

      public static class XRenderDirectFormat extends Structure {
         public short red;
         public short redMask;
         public short green;
         public short greenMask;
         public short blue;
         public short blueMask;
         public short alpha;
         public short alphaMask;
      }

      public static class XRenderPictFormat extends Structure {
         public X11.Xrender.PictFormat id;
         public int type;
         public int depth;
         public X11.Xrender.XRenderDirectFormat direct;
         public X11.Colormap colormap;
      }
   }
}
