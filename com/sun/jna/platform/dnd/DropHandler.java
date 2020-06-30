package com.sun.jna.platform.dnd;

import com.sun.jna.platform.dnd.DragHandler;
import com.sun.jna.platform.dnd.DropTargetPainter;
import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class DropHandler implements DropTargetListener {
   private int acceptedActions;
   private List acceptedFlavors;
   private DropTarget dropTarget;
   private boolean active;
   private DropTargetPainter painter;
   private String lastAction;

   public DropHandler(Component c, int acceptedActions) {
      this(c, acceptedActions, new DataFlavor[0]);
   }

   public DropHandler(Component c, int acceptedActions, DataFlavor[] acceptedFlavors) {
      this(c, acceptedActions, acceptedFlavors, (DropTargetPainter)null);
   }

   public DropHandler(Component c, int acceptedActions, DataFlavor[] acceptedFlavors, DropTargetPainter painter) {
      this.active = true;
      this.acceptedActions = acceptedActions;
      this.acceptedFlavors = Arrays.asList(acceptedFlavors);
      this.painter = painter;
      this.dropTarget = new DropTarget(c, acceptedActions, this, this.active);
   }

   protected DropTarget getDropTarget() {
      return this.dropTarget;
   }

   public boolean isActive() {
      return this.active;
   }

   public void setActive(boolean active) {
      this.active = active;
      if(this.dropTarget != null) {
         this.dropTarget.setActive(active);
      }

   }

   protected int getDropActionsForFlavors(DataFlavor[] dataFlavors) {
      return this.acceptedActions;
   }

   protected int getDropAction(DropTargetEvent e) {
      int currentAction = 0;
      int sourceActions = 0;
      Point location = null;
      DataFlavor[] flavors = new DataFlavor[0];
      if(e instanceof DropTargetDragEvent) {
         DropTargetDragEvent ev = (DropTargetDragEvent)e;
         currentAction = ev.getDropAction();
         sourceActions = ev.getSourceActions();
         flavors = ev.getCurrentDataFlavors();
         location = ev.getLocation();
      } else if(e instanceof DropTargetDropEvent) {
         DropTargetDropEvent ev = (DropTargetDropEvent)e;
         currentAction = ev.getDropAction();
         sourceActions = ev.getSourceActions();
         flavors = ev.getCurrentDataFlavors();
         location = ev.getLocation();
      }

      if(this.isSupported(flavors)) {
         int availableActions = this.getDropActionsForFlavors(flavors);
         currentAction = this.getDropAction(e, currentAction, sourceActions, availableActions);
         if(currentAction != 0 && this.canDrop(e, currentAction, location)) {
            return currentAction;
         }
      }

      return 0;
   }

   protected int getDropAction(DropTargetEvent e, int currentAction, int sourceActions, int acceptedActions) {
      boolean modifiersActive = this.modifiersActive(currentAction);
      if((currentAction & acceptedActions) == 0 && !modifiersActive) {
         int action = acceptedActions & sourceActions;
         currentAction = action;
      } else if(modifiersActive) {
         int action = currentAction & acceptedActions & sourceActions;
         if(action != currentAction) {
            currentAction = action;
         }
      }

      return currentAction;
   }

   protected boolean modifiersActive(int dropAction) {
      int mods = DragHandler.getModifiers();
      return mods == -1?dropAction == 1073741824 || dropAction == 1:mods != 0;
   }

   private void describe(String type, DropTargetEvent e) {
   }

   protected int acceptOrReject(DropTargetDragEvent e) {
      int action = this.getDropAction(e);
      if(action != 0) {
         e.acceptDrag(action);
      } else {
         e.rejectDrag();
      }

      return action;
   }

   public void dragEnter(DropTargetDragEvent e) {
      this.describe("enter(tgt)", e);
      int action = this.acceptOrReject(e);
      this.paintDropTarget(e, action, e.getLocation());
   }

   public void dragOver(DropTargetDragEvent e) {
      this.describe("over(tgt)", e);
      int action = this.acceptOrReject(e);
      this.paintDropTarget(e, action, e.getLocation());
   }

   public void dragExit(DropTargetEvent e) {
      this.describe("exit(tgt)", e);
      this.paintDropTarget(e, 0, (Point)null);
   }

   public void dropActionChanged(DropTargetDragEvent e) {
      this.describe("change(tgt)", e);
      int action = this.acceptOrReject(e);
      this.paintDropTarget(e, action, e.getLocation());
   }

   public void drop(DropTargetDropEvent e) {
      this.describe("drop(tgt)", e);
      int action = this.getDropAction(e);
      if(action != 0) {
         e.acceptDrop(action);

         try {
            this.drop(e, action);
            e.dropComplete(true);
         } catch (Exception var4) {
            e.dropComplete(false);
         }
      } else {
         e.rejectDrop();
      }

      this.paintDropTarget(e, 0, e.getLocation());
   }

   protected boolean isSupported(DataFlavor[] flavors) {
      Set<DataFlavor> set = new HashSet(Arrays.asList(flavors));
      set.retainAll(this.acceptedFlavors);
      return !set.isEmpty();
   }

   protected void paintDropTarget(DropTargetEvent e, int action, Point location) {
      if(this.painter != null) {
         this.painter.paintDropTarget(e, action, location);
      }

   }

   protected boolean canDrop(DropTargetEvent e, int action, Point location) {
      return true;
   }

   protected abstract void drop(DropTargetDropEvent var1, int var2) throws UnsupportedFlavorException, IOException;
}
