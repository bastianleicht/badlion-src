package org.lwjgl.input;

import java.util.ArrayList;
import net.java.games.input.Component;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import net.java.games.input.Rumbler;
import org.lwjgl.input.Controller;
import org.lwjgl.input.ControllerEvent;
import org.lwjgl.input.Controllers;

class JInputController implements Controller {
   private net.java.games.input.Controller target;
   private int index;
   private ArrayList buttons = new ArrayList();
   private ArrayList axes = new ArrayList();
   private ArrayList pov = new ArrayList();
   private Rumbler[] rumblers;
   private boolean[] buttonState;
   private float[] povValues;
   private float[] axesValue;
   private float[] axesMax;
   private float[] deadZones;
   private int xaxis = -1;
   private int yaxis = -1;
   private int zaxis = -1;
   private int rxaxis = -1;
   private int ryaxis = -1;
   private int rzaxis = -1;

   JInputController(int index, net.java.games.input.Controller target) {
      this.target = target;
      this.index = index;
      Component[] sourceAxes = target.getComponents();

      for(Component sourceAxis : sourceAxes) {
         if(sourceAxis.getIdentifier() instanceof Component.Identifier.Button) {
            this.buttons.add(sourceAxis);
         } else if(sourceAxis.getIdentifier().equals(Component.Identifier.Axis.POV)) {
            this.pov.add(sourceAxis);
         } else {
            this.axes.add(sourceAxis);
         }
      }

      this.buttonState = new boolean[this.buttons.size()];
      this.povValues = new float[this.pov.size()];
      this.axesValue = new float[this.axes.size()];
      int buttonsCount = 0;
      int axesCount = 0;

      for(Component sourceAxis : sourceAxes) {
         if(sourceAxis.getIdentifier() instanceof Component.Identifier.Button) {
            this.buttonState[buttonsCount] = sourceAxis.getPollData() != 0.0F;
            ++buttonsCount;
         } else if(!sourceAxis.getIdentifier().equals(Component.Identifier.Axis.POV)) {
            this.axesValue[axesCount] = sourceAxis.getPollData();
            if(sourceAxis.getIdentifier().equals(Component.Identifier.Axis.X)) {
               this.xaxis = axesCount;
            }

            if(sourceAxis.getIdentifier().equals(Component.Identifier.Axis.Y)) {
               this.yaxis = axesCount;
            }

            if(sourceAxis.getIdentifier().equals(Component.Identifier.Axis.Z)) {
               this.zaxis = axesCount;
            }

            if(sourceAxis.getIdentifier().equals(Component.Identifier.Axis.RX)) {
               this.rxaxis = axesCount;
            }

            if(sourceAxis.getIdentifier().equals(Component.Identifier.Axis.RY)) {
               this.ryaxis = axesCount;
            }

            if(sourceAxis.getIdentifier().equals(Component.Identifier.Axis.RZ)) {
               this.rzaxis = axesCount;
            }

            ++axesCount;
         }
      }

      this.axesMax = new float[this.axes.size()];
      this.deadZones = new float[this.axes.size()];

      for(int i = 0; i < this.axesMax.length; ++i) {
         this.axesMax[i] = 1.0F;
         this.deadZones[i] = 0.05F;
      }

      this.rumblers = target.getRumblers();
   }

   public String getName() {
      String name = this.target.getName();
      return name;
   }

   public int getIndex() {
      return this.index;
   }

   public int getButtonCount() {
      return this.buttons.size();
   }

   public String getButtonName(int index) {
      return ((Component)this.buttons.get(index)).getName();
   }

   public boolean isButtonPressed(int index) {
      return this.buttonState[index];
   }

   public void poll() {
      this.target.poll();
      Event event = new Event();
      EventQueue queue = this.target.getEventQueue();

      while(queue.getNextEvent(event)) {
         if(this.buttons.contains(event.getComponent())) {
            Component button = event.getComponent();
            int buttonIndex = this.buttons.indexOf(button);
            this.buttonState[buttonIndex] = event.getValue() != 0.0F;
            Controllers.addEvent(new ControllerEvent(this, event.getNanos(), 1, buttonIndex, this.buttonState[buttonIndex], false, false, 0.0F, 0.0F));
         }

         if(this.pov.contains(event.getComponent())) {
            Component povComponent = event.getComponent();
            int povIndex = this.pov.indexOf(povComponent);
            float prevX = this.getPovX();
            float prevY = this.getPovY();
            this.povValues[povIndex] = event.getValue();
            if(prevX != this.getPovX()) {
               Controllers.addEvent(new ControllerEvent(this, event.getNanos(), 3, 0, false, false));
            }

            if(prevY != this.getPovY()) {
               Controllers.addEvent(new ControllerEvent(this, event.getNanos(), 4, 0, false, false));
            }
         }

         if(this.axes.contains(event.getComponent())) {
            Component axis = event.getComponent();
            int axisIndex = this.axes.indexOf(axis);
            float value = axis.getPollData();
            float xaxisValue = 0.0F;
            float yaxisValue = 0.0F;
            if(Math.abs(value) < this.deadZones[axisIndex]) {
               value = 0.0F;
            }

            if(Math.abs(value) < axis.getDeadZone()) {
               value = 0.0F;
            }

            if(Math.abs(value) > this.axesMax[axisIndex]) {
               this.axesMax[axisIndex] = Math.abs(value);
            }

            value = value / this.axesMax[axisIndex];
            if(axisIndex == this.xaxis) {
               xaxisValue = value;
            }

            if(axisIndex == this.yaxis) {
               yaxisValue = value;
            }

            Controllers.addEvent(new ControllerEvent(this, event.getNanos(), 2, axisIndex, false, axisIndex == this.xaxis, axisIndex == this.yaxis, xaxisValue, yaxisValue));
            this.axesValue[axisIndex] = value;
         }
      }

   }

   public int getAxisCount() {
      return this.axes.size();
   }

   public String getAxisName(int index) {
      return ((Component)this.axes.get(index)).getName();
   }

   public float getAxisValue(int index) {
      return this.axesValue[index];
   }

   public float getXAxisValue() {
      return this.xaxis == -1?0.0F:this.getAxisValue(this.xaxis);
   }

   public float getYAxisValue() {
      return this.yaxis == -1?0.0F:this.getAxisValue(this.yaxis);
   }

   public float getXAxisDeadZone() {
      return this.xaxis == -1?0.0F:this.getDeadZone(this.xaxis);
   }

   public float getYAxisDeadZone() {
      return this.yaxis == -1?0.0F:this.getDeadZone(this.yaxis);
   }

   public void setXAxisDeadZone(float zone) {
      this.setDeadZone(this.xaxis, zone);
   }

   public void setYAxisDeadZone(float zone) {
      this.setDeadZone(this.yaxis, zone);
   }

   public float getDeadZone(int index) {
      return this.deadZones[index];
   }

   public void setDeadZone(int index, float zone) {
      this.deadZones[index] = zone;
   }

   public float getZAxisValue() {
      return this.zaxis == -1?0.0F:this.getAxisValue(this.zaxis);
   }

   public float getZAxisDeadZone() {
      return this.zaxis == -1?0.0F:this.getDeadZone(this.zaxis);
   }

   public void setZAxisDeadZone(float zone) {
      this.setDeadZone(this.zaxis, zone);
   }

   public float getRXAxisValue() {
      return this.rxaxis == -1?0.0F:this.getAxisValue(this.rxaxis);
   }

   public float getRXAxisDeadZone() {
      return this.rxaxis == -1?0.0F:this.getDeadZone(this.rxaxis);
   }

   public void setRXAxisDeadZone(float zone) {
      this.setDeadZone(this.rxaxis, zone);
   }

   public float getRYAxisValue() {
      return this.ryaxis == -1?0.0F:this.getAxisValue(this.ryaxis);
   }

   public float getRYAxisDeadZone() {
      return this.ryaxis == -1?0.0F:this.getDeadZone(this.ryaxis);
   }

   public void setRYAxisDeadZone(float zone) {
      this.setDeadZone(this.ryaxis, zone);
   }

   public float getRZAxisValue() {
      return this.rzaxis == -1?0.0F:this.getAxisValue(this.rzaxis);
   }

   public float getRZAxisDeadZone() {
      return this.rzaxis == -1?0.0F:this.getDeadZone(this.rzaxis);
   }

   public void setRZAxisDeadZone(float zone) {
      this.setDeadZone(this.rzaxis, zone);
   }

   public float getPovX() {
      if(this.pov.size() == 0) {
         return 0.0F;
      } else {
         float value = this.povValues[0];
         return value != 0.875F && value != 0.125F && value != 1.0F?(value != 0.625F && value != 0.375F && value != 0.5F?0.0F:1.0F):-1.0F;
      }
   }

   public float getPovY() {
      if(this.pov.size() == 0) {
         return 0.0F;
      } else {
         float value = this.povValues[0];
         return value != 0.875F && value != 0.625F && value != 0.75F?(value != 0.125F && value != 0.375F && value != 0.25F?0.0F:-1.0F):1.0F;
      }
   }

   public int getRumblerCount() {
      return this.rumblers.length;
   }

   public String getRumblerName(int index) {
      return this.rumblers[index].getAxisName();
   }

   public void setRumblerStrength(int index, float strength) {
      this.rumblers[index].rumble(strength);
   }
}
