package com.sun.jna.platform.dnd;

import com.sun.jna.platform.dnd.GhostedDragImage;
import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DragSourceMotionListener;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.text.JTextComponent;

public abstract class DragHandler implements DragSourceListener, DragSourceMotionListener, DragGestureListener {
   public static final Dimension MAX_GHOST_SIZE = new Dimension(250, 250);
   public static final float DEFAULT_GHOST_ALPHA = 0.5F;
   public static final int UNKNOWN_MODIFIERS = -1;
   public static final Transferable UNKNOWN_TRANSFERABLE = null;
   protected static final int MOVE = 2;
   protected static final int COPY = 1;
   protected static final int LINK = 1073741824;
   protected static final int NONE = 0;
   static final int MOVE_MASK = 64;
   static final boolean OSX = System.getProperty("os.name").toLowerCase().indexOf("mac") != -1;
   static final int COPY_MASK = OSX?512:128;
   static final int LINK_MASK = OSX?768:192;
   static final int KEY_MASK = 9152;
   private static int modifiers = -1;
   private static Transferable transferable = UNKNOWN_TRANSFERABLE;
   private int supportedActions;
   private boolean fixCursor = true;
   private Component dragSource;
   private GhostedDragImage ghost;
   private Point imageOffset;
   private Dimension maxGhostSize;
   private float ghostAlpha;
   private String lastAction;
   private boolean moved;

   static int getModifiers() {
      return modifiers;
   }

   public static Transferable getTransferable(DropTargetEvent e) {
      if(e instanceof DropTargetDragEvent) {
         try {
            return (Transferable)e.getClass().getMethod("getTransferable", (Class[])null).invoke(e, (Object[])null);
         } catch (Exception var2) {
            ;
         }
      } else if(e instanceof DropTargetDropEvent) {
         return ((DropTargetDropEvent)e).getTransferable();
      }

      return transferable;
   }

   protected DragHandler(Component dragSource, int actions) {
      this.maxGhostSize = MAX_GHOST_SIZE;
      this.ghostAlpha = 0.5F;
      this.dragSource = dragSource;
      this.supportedActions = actions;

      try {
         String alpha = System.getProperty("DragHandler.alpha");
         if(alpha != null) {
            try {
               this.ghostAlpha = Float.parseFloat(alpha);
            } catch (NumberFormatException var8) {
               ;
            }
         }

         String max = System.getProperty("DragHandler.maxDragImageSize");
         if(max != null) {
            String[] size = max.split("x");
            if(size.length == 2) {
               try {
                  this.maxGhostSize = new Dimension(Integer.parseInt(size[0]), Integer.parseInt(size[1]));
               } catch (NumberFormatException var7) {
                  ;
               }
            }
         }
      } catch (SecurityException var9) {
         ;
      }

      this.disableSwingDragSupport(dragSource);
      DragSource src = DragSource.getDefaultDragSource();
      src.createDefaultDragGestureRecognizer(dragSource, this.supportedActions, this);
   }

   private void disableSwingDragSupport(Component comp) {
      if(comp instanceof JTree) {
         ((JTree)comp).setDragEnabled(false);
      } else if(comp instanceof JList) {
         ((JList)comp).setDragEnabled(false);
      } else if(comp instanceof JTable) {
         ((JTable)comp).setDragEnabled(false);
      } else if(comp instanceof JTextComponent) {
         ((JTextComponent)comp).setDragEnabled(false);
      } else if(comp instanceof JColorChooser) {
         ((JColorChooser)comp).setDragEnabled(false);
      } else if(comp instanceof JFileChooser) {
         ((JFileChooser)comp).setDragEnabled(false);
      }

   }

   protected boolean canDrag(DragGestureEvent e) {
      int mods = e.getTriggerEvent().getModifiersEx() & 9152;
      return mods == 64?(this.supportedActions & 2) != 0:(mods == COPY_MASK?(this.supportedActions & 1) != 0:(mods == LINK_MASK?(this.supportedActions & 1073741824) != 0:true));
   }

   protected void setModifiers(int mods) {
      modifiers = mods;
   }

   protected abstract Transferable getTransferable(DragGestureEvent var1);

   protected Icon getDragIcon(DragGestureEvent e, Point srcOffset) {
      return null;
   }

   protected void dragStarted(DragGestureEvent e) {
   }

   public void dragGestureRecognized(DragGestureEvent e) {
      if((e.getDragAction() & this.supportedActions) != 0 && this.canDrag(e)) {
         this.setModifiers(e.getTriggerEvent().getModifiersEx() & 9152);
         Transferable transferable = this.getTransferable(e);
         if(transferable == null) {
            return;
         }

         try {
            Point srcOffset = new Point(0, 0);
            Icon icon = this.getDragIcon(e, srcOffset);
            Point origin = e.getDragOrigin();
            this.imageOffset = new Point(srcOffset.x - origin.x, srcOffset.y - origin.y);
            Icon dragIcon = this.scaleDragIcon(icon, this.imageOffset);
            Cursor cursor = null;
            if(dragIcon != null && DragSource.isDragImageSupported()) {
               GraphicsConfiguration gc = e.getComponent().getGraphicsConfiguration();
               e.startDrag(cursor, this.createDragImage(gc, dragIcon), this.imageOffset, transferable, this);
            } else {
               if(dragIcon != null) {
                  Point screen = this.dragSource.getLocationOnScreen();
                  screen.translate(origin.x, origin.y);
                  Point cursorOffset = new Point(-this.imageOffset.x, -this.imageOffset.y);
                  this.ghost = new GhostedDragImage(this.dragSource, dragIcon, this.getImageLocation(screen), cursorOffset);
                  this.ghost.setAlpha(this.ghostAlpha);
               }

               e.startDrag(cursor, transferable, this);
            }

            this.dragStarted(e);
            this.moved = false;
            e.getDragSource().addDragSourceMotionListener(this);
            transferable = transferable;
         } catch (InvalidDnDOperationException var10) {
            if(this.ghost != null) {
               this.ghost.dispose();
               this.ghost = null;
            }
         }
      }

   }

   protected Icon scaleDragIcon(Icon icon, Point imageOffset) {
      return icon;
   }

   protected Image createDragImage(GraphicsConfiguration gc, Icon icon) {
      int w = icon.getIconWidth();
      int h = icon.getIconHeight();
      BufferedImage image = gc.createCompatibleImage(w, h, 3);
      Graphics2D g = (Graphics2D)image.getGraphics();
      g.setComposite(AlphaComposite.Clear);
      g.fillRect(0, 0, w, h);
      g.setComposite(AlphaComposite.getInstance(2, this.ghostAlpha));
      icon.paintIcon(this.dragSource, g, 0, 0);
      g.dispose();
      return image;
   }

   private int reduce(int actions) {
      return (actions & 2) != 0 && actions != 2?2:((actions & 1) != 0 && actions != 1?1:actions);
   }

   protected Cursor getCursorForAction(int actualAction) {
      switch(actualAction) {
      case 1:
         return DragSource.DefaultCopyDrop;
      case 2:
         return DragSource.DefaultMoveDrop;
      case 1073741824:
         return DragSource.DefaultLinkDrop;
      default:
         return DragSource.DefaultMoveNoDrop;
      }
   }

   protected int getAcceptableDropAction(int targetActions) {
      return this.reduce(this.supportedActions & targetActions);
   }

   protected int getDropAction(DragSourceEvent ev) {
      if(ev instanceof DragSourceDragEvent) {
         DragSourceDragEvent e = (DragSourceDragEvent)ev;
         return e.getDropAction();
      } else {
         return ev instanceof DragSourceDropEvent?((DragSourceDropEvent)ev).getDropAction():0;
      }
   }

   protected int adjustDropAction(DragSourceEvent ev) {
      int action = this.getDropAction(ev);
      if(ev instanceof DragSourceDragEvent) {
         DragSourceDragEvent e = (DragSourceDragEvent)ev;
         if(action == 0) {
            int mods = e.getGestureModifiersEx() & 9152;
            if(mods == 0) {
               action = this.getAcceptableDropAction(e.getTargetActions());
            }
         }
      }

      return action;
   }

   protected void updateCursor(DragSourceEvent ev) {
      if(this.fixCursor) {
         Cursor cursor = this.getCursorForAction(this.adjustDropAction(ev));
         ev.getDragSourceContext().setCursor(cursor);
      }
   }

   static String actionString(int action) {
      switch(action) {
      case 1:
         return "COPY";
      case 2:
         return "MOVE";
      case 3:
         return "MOVE|COPY";
      case 1073741824:
         return "LINK";
      case 1073741825:
         return "COPY|LINK";
      case 1073741826:
         return "MOVE|LINK";
      case 1073741827:
         return "MOVE|COPY|LINK";
      default:
         return "NONE";
      }
   }

   private void describe(String type, DragSourceEvent e) {
   }

   public void dragDropEnd(DragSourceDropEvent e) {
      this.describe("end", e);
      this.setModifiers(-1);
      transferable = UNKNOWN_TRANSFERABLE;
      if(this.ghost != null) {
         if(e.getDropSuccess()) {
            this.ghost.dispose();
         } else {
            this.ghost.returnToOrigin();
         }

         this.ghost = null;
      }

      DragSource src = e.getDragSourceContext().getDragSource();
      src.removeDragSourceMotionListener(this);
      this.moved = false;
   }

   private Point getImageLocation(Point where) {
      where.translate(this.imageOffset.x, this.imageOffset.y);
      return where;
   }

   public void dragEnter(DragSourceDragEvent e) {
      this.describe("enter", e);
      if(this.ghost != null) {
         this.ghost.move(this.getImageLocation(e.getLocation()));
      }

      this.updateCursor(e);
   }

   public void dragMouseMoved(DragSourceDragEvent e) {
      this.describe("move", e);
      if(this.ghost != null) {
         this.ghost.move(this.getImageLocation(e.getLocation()));
      }

      if(this.moved) {
         this.updateCursor(e);
      }

      this.moved = true;
   }

   public void dragOver(DragSourceDragEvent e) {
      this.describe("over", e);
      if(this.ghost != null) {
         this.ghost.move(this.getImageLocation(e.getLocation()));
      }

      this.updateCursor(e);
   }

   public void dragExit(DragSourceEvent e) {
      this.describe("exit", e);
   }

   public void dropActionChanged(DragSourceDragEvent e) {
      this.describe("change", e);
      this.setModifiers(e.getGestureModifiersEx() & 9152);
      if(this.ghost != null) {
         this.ghost.move(this.getImageLocation(e.getLocation()));
      }

      this.updateCursor(e);
   }
}
