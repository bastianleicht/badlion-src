package com.mojang.realmsclient.gui.screens;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.ServerActivity;
import com.mojang.realmsclient.dto.ServerActivityList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsDefaultVertexFormat;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsScrolledSelectionList;
import net.minecraft.realms.Tezzelator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class RealmsActivityScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsScreen lastScreen;
   private final RealmsServer serverData;
   private volatile List activityMap = new ArrayList();
   private RealmsActivityScreen.DetailsList list;
   private int matrixWidth;
   private String toolTip;
   private volatile List dayList = new ArrayList();
   private List colors = Arrays.asList(new RealmsActivityScreen.Color[]{new RealmsActivityScreen.Color(79, 243, 29), new RealmsActivityScreen.Color(243, 175, 29), new RealmsActivityScreen.Color(243, 29, 190), new RealmsActivityScreen.Color(29, 165, 243), new RealmsActivityScreen.Color(29, 243, 130), new RealmsActivityScreen.Color(243, 29, 64), new RealmsActivityScreen.Color(29, 74, 243)});
   private int colorIndex = 0;
   private long periodInMillis;
   private int maxKeyWidth = 0;
   private Boolean noActivity = Boolean.valueOf(false);
   private int activityPoint = 0;
   private int dayWidth = 0;
   private double hourWidth = 0.0D;
   private double minuteWidth = 0.0D;
   private int BUTTON_BACK_ID = 0;
   private static LoadingCache activitiesNameCache = CacheBuilder.newBuilder().build(new CacheLoader() {
      public String load(String uuid) throws Exception {
         String name = Realms.uuidToName(uuid);
         if(name == null) {
            throw new Exception("Couldn\'t get username");
         } else {
            return name;
         }
      }
   });

   public RealmsActivityScreen(RealmsScreen lastScreen, RealmsServer serverData) {
      this.lastScreen = lastScreen;
      this.serverData = serverData;
      this.getActivities();
   }

   public void mouseEvent() {
      super.mouseEvent();
      this.list.mouseEvent();
   }

   public void init() {
      Keyboard.enableRepeatEvents(true);
      this.buttonsClear();
      this.matrixWidth = this.width();
      this.list = new RealmsActivityScreen.DetailsList();
      this.buttonsAdd(newButton(this.BUTTON_BACK_ID, this.width() / 2 - 100, this.height() - 30, 200, 20, getLocalizedString("gui.back")));
   }

   private RealmsActivityScreen.Color getColor() {
      if(this.colorIndex > this.colors.size() - 1) {
         this.colorIndex = 0;
      }

      return (RealmsActivityScreen.Color)this.colors.get(this.colorIndex++);
   }

   private void getActivities() {
      (new Thread() {
         public void run() {
            RealmsClient client = RealmsClient.createRealmsClient();

            try {
               ServerActivityList activities = client.getActivity(RealmsActivityScreen.this.serverData.id);
               RealmsActivityScreen.this.activityMap = RealmsActivityScreen.this.convertToActivityMatrix(activities);
               List<RealmsActivityScreen.Day> tempDayList = new ArrayList();

               for(RealmsActivityScreen.ActivityRow row : RealmsActivityScreen.this.activityMap) {
                  for(RealmsActivityScreen.Activity activity : row.activities) {
                     String day = (new SimpleDateFormat("dd/MM")).format(new Date(activity.start));
                     RealmsActivityScreen.Day the_day = new RealmsActivityScreen.Day(day, Long.valueOf(activity.start));
                     if(!tempDayList.contains(the_day)) {
                        tempDayList.add(the_day);
                     }
                  }
               }

               Collections.sort(tempDayList);

               for(RealmsActivityScreen.ActivityRow row : RealmsActivityScreen.this.activityMap) {
                  for(RealmsActivityScreen.Activity activity : row.activities) {
                     String day = (new SimpleDateFormat("dd/MM")).format(new Date(activity.start));
                     RealmsActivityScreen.Day the_day = new RealmsActivityScreen.Day(day, Long.valueOf(activity.start));
                     activity.dayIndex = tempDayList.indexOf(the_day) + 1;
                  }
               }

               RealmsActivityScreen.this.dayList = tempDayList;
            } catch (RealmsServiceException var10) {
               var10.printStackTrace();
            }

         }
      }).start();
   }

   private List convertToActivityMatrix(ServerActivityList serverActivityList) {
      List<RealmsActivityScreen.ActivityRow> activityRows = Lists.newArrayList();
      this.periodInMillis = serverActivityList.periodInMillis;
      long base = System.currentTimeMillis() - serverActivityList.periodInMillis;

      for(ServerActivity sa : serverActivityList.serverActivities) {
         RealmsActivityScreen.ActivityRow activityRow = this.find(sa.profileUuid, activityRows);
         Calendar joinTime = Calendar.getInstance(TimeZone.getDefault());
         joinTime.setTimeInMillis(sa.joinTime);
         Calendar leaveTime = Calendar.getInstance(TimeZone.getDefault());
         leaveTime.setTimeInMillis(sa.leaveTime);
         RealmsActivityScreen.Activity e = new RealmsActivityScreen.Activity(base, joinTime.getTimeInMillis(), leaveTime.getTimeInMillis());
         if(activityRow == null) {
            String name = "";

            try {
               name = (String)activitiesNameCache.get(sa.profileUuid);
            } catch (Exception var13) {
               LOGGER.error((String)("Could not get name for " + sa.profileUuid), (Throwable)var13);
               continue;
            }

            activityRow = new RealmsActivityScreen.ActivityRow(sa.profileUuid, new ArrayList(), name, sa.profileUuid);
            activityRow.activities.add(e);
            activityRows.add(activityRow);
         } else {
            activityRow.activities.add(e);
         }
      }

      Collections.sort(activityRows);

      for(RealmsActivityScreen.ActivityRow row : activityRows) {
         row.color = this.getColor();
         Collections.sort(row.activities);
      }

      this.noActivity = Boolean.valueOf(activityRows.size() == 0);
      return activityRows;
   }

   private RealmsActivityScreen.ActivityRow find(String key, List rows) {
      for(RealmsActivityScreen.ActivityRow row : rows) {
         if(row.key.equals(key)) {
            return row;
         }
      }

      return null;
   }

   public void tick() {
      super.tick();
   }

   public void buttonClicked(RealmsButton button) {
      if(button.id() == this.BUTTON_BACK_ID) {
         Realms.setScreen(this.lastScreen);
      }

   }

   public void keyPressed(char ch, int eventKey) {
      if(eventKey == 1) {
         Realms.setScreen(this.lastScreen);
      }

   }

   public void render(int xm, int ym, float a) {
      this.toolTip = null;
      this.renderBackground();

      for(RealmsActivityScreen.ActivityRow row : this.activityMap) {
         int keyWidth = this.fontWidth(row.name);
         if(keyWidth > this.maxKeyWidth) {
            this.maxKeyWidth = keyWidth + 10;
         }
      }

      int keyRightPadding = 25;
      this.activityPoint = this.maxKeyWidth + keyRightPadding;
      int spaceLeft = this.matrixWidth - this.activityPoint - 10;
      int days = this.dayList.size() < 1?1:this.dayList.size();
      this.dayWidth = spaceLeft / days;
      this.hourWidth = (double)this.dayWidth / 24.0D;
      this.minuteWidth = this.hourWidth / 60.0D;
      this.list.render(xm, ym, a);
      if(this.activityMap != null && this.activityMap.size() > 0) {
         Tezzelator t = Tezzelator.instance;
         GL11.glDisable(3553);
         t.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
         t.vertex((double)this.activityPoint, (double)(this.height() - 40), 0.0D).color(128, 128, 128, 255).endVertex();
         t.vertex((double)(this.activityPoint + 1), (double)(this.height() - 40), 0.0D).color(128, 128, 128, 255).endVertex();
         t.vertex((double)(this.activityPoint + 1), 30.0D, 0.0D).color(128, 128, 128, 255).endVertex();
         t.vertex((double)this.activityPoint, 30.0D, 0.0D).color(128, 128, 128, 255).endVertex();
         t.end();
         GL11.glEnable(3553);

         for(RealmsActivityScreen.Day day : this.dayList) {
            int daysIndex = this.dayList.indexOf(day) + 1;
            this.drawString(day.day, this.activityPoint + (daysIndex - 1) * this.dayWidth + (this.dayWidth - this.fontWidth(day.day)) / 2 + 2, this.height() - 52, 16777215);
            GL11.glDisable(3553);
            t.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
            t.vertex((double)(this.activityPoint + daysIndex * this.dayWidth), (double)(this.height() - 40), 0.0D).color(128, 128, 128, 255).endVertex();
            t.vertex((double)(this.activityPoint + daysIndex * this.dayWidth + 1), (double)(this.height() - 40), 0.0D).color(128, 128, 128, 255).endVertex();
            t.vertex((double)(this.activityPoint + daysIndex * this.dayWidth + 1), 30.0D, 0.0D).color(128, 128, 128, 255).endVertex();
            t.vertex((double)(this.activityPoint + daysIndex * this.dayWidth), 30.0D, 0.0D).color(128, 128, 128, 255).endVertex();
            t.end();
            GL11.glEnable(3553);
         }
      }

      super.render(xm, ym, a);
      this.drawCenteredString(getLocalizedString("mco.activity.title"), this.width() / 2, 10, 16777215);
      if(this.toolTip != null) {
         this.renderMousehoverTooltip(this.toolTip, xm, ym);
      }

      if(this.noActivity.booleanValue()) {
         this.drawCenteredString(getLocalizedString("mco.activity.noactivity", new Object[]{Long.valueOf(TimeUnit.DAYS.convert(this.periodInMillis, TimeUnit.MILLISECONDS))}), this.width() / 2, this.height() / 2 - 20, 16777215);
      }

   }

   protected void renderMousehoverTooltip(String msg, int x, int y) {
      if(msg != null) {
         int index = 0;
         int width = 0;

         for(String s : msg.split("\n")) {
            int the_width = this.fontWidth(s);
            if(the_width > width) {
               width = the_width;
            }
         }

         int rx = x - width - 5;
         int ry = y;
         if(rx < 0) {
            rx = x + 12;
         }

         for(String s : msg.split("\n")) {
            this.fillGradient(rx - 3, ry - (index == 0?3:0) + index, rx + width + 3, ry + 8 + 3 + index, -1073741824, -1073741824);
            this.fontDrawShadow(s, rx, ry + index, -1);
            index += 10;
         }

      }
   }

   static class Activity implements Comparable {
      long base;
      long start;
      long end;
      int dayIndex;

      private Activity(long base, long start, long end) {
         this.base = base;
         this.start = start;
         this.end = end;
      }

      public int compareTo(RealmsActivityScreen.Activity o) {
         return (int)(this.start - o.start);
      }

      public int hourIndice() {
         String hour = (new SimpleDateFormat("HH")).format(new Date(this.start));
         return Integer.parseInt(hour);
      }

      public int minuteIndice() {
         String minute = (new SimpleDateFormat("mm")).format(new Date(this.start));
         return Integer.parseInt(minute);
      }
   }

   static class ActivityRender {
      double start;
      double width;
      String tooltip;

      private ActivityRender(double start, double width, String tooltip) {
         this.start = start;
         this.width = width;
         this.tooltip = tooltip;
      }
   }

   static class ActivityRow implements Comparable {
      String key;
      List activities;
      RealmsActivityScreen.Color color;
      String name;
      String uuid;

      public int compareTo(RealmsActivityScreen.ActivityRow o) {
         return this.name.compareTo(o.name);
      }

      ActivityRow(String key, List activities, String name, String uuid) {
         this.key = key;
         this.activities = activities;
         this.name = name;
         this.uuid = uuid;
      }
   }

   static class Color {
      int r;
      int g;
      int b;

      Color(int r, int g, int b) {
         this.r = r;
         this.g = g;
         this.b = b;
      }
   }

   static class Day implements Comparable {
      String day;
      Long timestamp;

      public int compareTo(RealmsActivityScreen.Day o) {
         return this.timestamp.compareTo(o.timestamp);
      }

      Day(String day, Long timestamp) {
         this.day = day;
         this.timestamp = timestamp;
      }

      public boolean equals(Object d) {
         if(!(d instanceof RealmsActivityScreen.Day)) {
            return false;
         } else {
            RealmsActivityScreen.Day that = (RealmsActivityScreen.Day)d;
            return this.day.equals(that.day);
         }
      }
   }

   class DetailsList extends RealmsScrolledSelectionList {
      public DetailsList() {
         super(RealmsActivityScreen.this.width(), RealmsActivityScreen.this.height(), 30, RealmsActivityScreen.this.height() - 40, RealmsActivityScreen.this.fontLineHeight() + 1);
      }

      public int getItemCount() {
         return RealmsActivityScreen.this.activityMap.size();
      }

      public void selectItem(int item, boolean doubleClick, int xMouse, int yMouse) {
      }

      public boolean isSelectedItem(int item) {
         return false;
      }

      public int getMaxPosition() {
         return this.getItemCount() * (RealmsActivityScreen.this.fontLineHeight() + 1) + 15;
      }

      protected void renderItem(int i, int x, int y, int h, Tezzelator t, int mouseX, int mouseY) {
         if(RealmsActivityScreen.this.activityMap != null && RealmsActivityScreen.this.activityMap.size() > i) {
            RealmsActivityScreen.ActivityRow row = (RealmsActivityScreen.ActivityRow)RealmsActivityScreen.this.activityMap.get(i);
            RealmsActivityScreen.this.drawString(row.name, 20, y, ((RealmsActivityScreen.ActivityRow)RealmsActivityScreen.this.activityMap.get(i)).uuid.equals(Realms.getUUID())?8388479:16777215);
            int r = row.color.r;
            int g = row.color.g;
            int b = row.color.b;
            GL11.glDisable(3553);
            t.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
            t.vertex((double)(RealmsActivityScreen.this.activityPoint - 8), (double)y + 6.5D, 0.0D).color(r, g, b, 255).endVertex();
            t.vertex((double)(RealmsActivityScreen.this.activityPoint - 3), (double)y + 6.5D, 0.0D).color(r, g, b, 255).endVertex();
            t.vertex((double)(RealmsActivityScreen.this.activityPoint - 3), (double)y + 1.5D, 0.0D).color(r, g, b, 255).endVertex();
            t.vertex((double)(RealmsActivityScreen.this.activityPoint - 8), (double)y + 1.5D, 0.0D).color(r, g, b, 255).endVertex();
            t.end();
            GL11.glEnable(3553);
            RealmsScreen.bindFace(((RealmsActivityScreen.ActivityRow)RealmsActivityScreen.this.activityMap.get(i)).uuid, ((RealmsActivityScreen.ActivityRow)RealmsActivityScreen.this.activityMap.get(i)).name);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            RealmsScreen.blit(10, y, 8.0F, 8.0F, 8, 8, 8, 8, 64.0F, 64.0F);
            RealmsScreen.blit(10, y, 40.0F, 8.0F, 8, 8, 8, 8, 64.0F, 64.0F);
            List<RealmsActivityScreen.ActivityRender> toRender = new ArrayList();

            for(RealmsActivityScreen.Activity activity : row.activities) {
               int minute = activity.minuteIndice();
               int hour = activity.hourIndice();
               double itemWidth = RealmsActivityScreen.this.minuteWidth * (double)TimeUnit.MINUTES.convert(activity.end - activity.start, TimeUnit.MILLISECONDS);
               if(itemWidth < 3.0D) {
                  itemWidth = 3.0D;
               }

               double pos = (double)(RealmsActivityScreen.this.activityPoint + (RealmsActivityScreen.this.dayWidth * activity.dayIndex - RealmsActivityScreen.this.dayWidth)) + (double)hour * RealmsActivityScreen.this.hourWidth + (double)minute * RealmsActivityScreen.this.minuteWidth;
               SimpleDateFormat format = new SimpleDateFormat("HH:mm");
               Date startDate = new Date(activity.start);
               Date endDate = new Date(activity.end);
               int length = (int)Math.ceil((double)TimeUnit.SECONDS.convert(activity.end - activity.start, TimeUnit.MILLISECONDS) / 60.0D);
               if(length < 1) {
                  length = 1;
               }

               String tooltip = "[" + format.format(startDate) + " - " + format.format(endDate) + "] " + length + (length > 1?" minutes":" minute");
               boolean exists = false;

               for(RealmsActivityScreen.ActivityRender render : toRender) {
                  if(render.start + render.width >= pos - 0.5D) {
                     double overlap = render.start + render.width - pos;
                     double padding = Math.max(0.0D, pos - (render.start + render.width));
                     render.width = render.width - Math.max(0.0D, overlap) + itemWidth + padding;
                     render.tooltip = render.tooltip + "\n" + tooltip;
                     exists = true;
                     break;
                  }
               }

               if(!exists) {
                  toRender.add(new RealmsActivityScreen.ActivityRender(pos, itemWidth, tooltip));
               }
            }

            for(RealmsActivityScreen.ActivityRender render : toRender) {
               GL11.glDisable(3553);
               t.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
               t.vertex(render.start, (double)y + 6.5D, 0.0D).color(r, g, b, 255).endVertex();
               t.vertex(render.start + render.width, (double)y + 6.5D, 0.0D).color(r, g, b, 255).endVertex();
               t.vertex(render.start + render.width, (double)y + 1.5D, 0.0D).color(r, g, b, 255).endVertex();
               t.vertex(render.start, (double)y + 1.5D, 0.0D).color(r, g, b, 255).endVertex();
               t.end();
               GL11.glEnable(3553);
               if((double)this.xm() >= render.start && (double)this.xm() <= render.start + render.width && (double)this.ym() >= (double)y + 1.5D && (double)this.ym() <= (double)y + 6.5D) {
                  RealmsActivityScreen.this.toolTip = render.tooltip.trim();
               }
            }
         }

      }

      public int getScrollbarPosition() {
         return this.width() - 7;
      }
   }
}
