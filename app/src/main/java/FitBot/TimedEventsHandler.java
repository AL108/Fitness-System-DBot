package FitBot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import java.util.concurrent.*;
import java.awt.*;


import javax.swing.text.AttributeSet.ColorAttribute;
import net.dv8tion.jda.api.entities.Member;
import com.google.common.util.concurrent.AbstractScheduledService.Scheduler;

import static java.util.concurrent.TimeUnit.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TimedEventsHandler {
    private final ScheduledExecutorService schedulerDaily = Executors.newScheduledThreadPool(1);
    private final ScheduledExecutorService schedulerWeekly = Executors.newScheduledThreadPool(1);
    private final ScheduledExecutorService schedulerEOC = Executors.newScheduledThreadPool(1);
    private final ScheduledExecutorService schedulerBreak = Executors.newScheduledThreadPool(3);
    //private final ScheduledExecutorService scheduleWeekly = Executors.newScheduledThreadPool(1);

    Runnable dailyUpdate;
    Runnable weeklyUpdate;
    Runnable eocUpdate;
    ScheduledFuture<?> dailyUpdateHandle;
    ScheduledFuture<?> weeklyUpdateHandle;
    ScheduledFuture<?> endOfCycleUpdateHandle;
    //Runnable stopUpdatesForCurrentCycle;
    Runnable stopDailyUpdates;
    Runnable stopWeeklyUpdates;
    //Runnable stopEOCUpdate;

    Runnable endBreak;
    ScheduledFuture<?> endBreakHandle;

    private Message generateDailyUpdate(SlashCommandEvent event) {
        ArrayList<String> uidsInCycle = new ArrayList<>();
        System.out.println("what thte fuck!");
        String curUser;
        for (int i = 0; i < event.getGuildChannel().getMembers().size(); i++) {
            curUser = event.getGuildChannel().getMembers().get(i).getId();
            if (DatabaseUtility.checkIfUserIsInCycle(curUser)) {
                uidsInCycle.add(curUser);
            }
        }

        StringBuilder todaysWorkoutsBuilder = new StringBuilder();
        StringBuilder freeDaysLeftBuilder = new StringBuilder();
        StringBuilder peopleCurrentlyOnBreakBuilder = new StringBuilder();
        for (int i = 0; i < uidsInCycle.size(); i++) {
            ArrayList<String> userWorkoutsToday = (ArrayList<String>) DatabaseUtility.getUserWorkoutsForToday(uidsInCycle.get(i));
            if (DatabaseUtility.getUserWorkoutsForToday(uidsInCycle.get(i)) != null) {
                todaysWorkoutsBuilder.append("**" + event.getGuild().getMemberById(uidsInCycle.get(i)).getEffectiveName() + "**\n");
                for (String workout : userWorkoutsToday) {
                    todaysWorkoutsBuilder.append("-"+workout+"\n");
                }
            }
            if (DatabaseUtility.checkUserIsOnBreak(uidsInCycle.get(i))) {
                peopleCurrentlyOnBreakBuilder.append("**" + event.getGuild().getMemberById(uidsInCycle.get(i)) + "** ");
            }
            
            freeDaysLeftBuilder.append("**" + event.getGuild().getMemberById(uidsInCycle.get(i)).getEffectiveName() + "** ");
            freeDaysLeftBuilder.append(String.valueOf(DatabaseUtility.getNumberOfRestDaysLeftForUser(uidsInCycle.get(i))) + "\n");
        }
        //peopleCurrentlyOnBreakBuilder.append("\n");
        String onBreak = peopleCurrentlyOnBreakBuilder.toString();
        if (onBreak == "") onBreak = "Nobody in the current cycle is on break.";
        String todaysWorkouts = todaysWorkoutsBuilder.toString();
        if (todaysWorkouts == null) todaysWorkouts = "No workouts were completed today.";
        return new MessageBuilder()
        .append("**Daily Update**")
        .setEmbeds(new EmbedBuilder()
            .setColor(new Color(16776960))
            //.addField("Today's workouts", "**Alien**\n-Strength training\n-5k run\n**2021-hero**\n-7k run\n-sets of pushups for 20 min", false)
            .addField("Today's workouts",todaysWorkouts,false)
            .addField("Rest days left", freeDaysLeftBuilder.toString(), false)
            .addField("People currently on break", onBreak, false)
            .build())
        .build();
    }

    /* private void loadMembers(SlashCommandEvent event) {
        event.getGuild().loadMembers();
    } */


    private void dailyUpdate(SlashCommandEvent event) {
        System.out.println("    YES");
        ArrayList<String> kickedUsers = new ArrayList<>();
        String curUser;
        //event.getGuildChannel().getMembers()
        //xw.run();
        
    
        //List<Member> members = event.getJDA().getGuildChannelById(ChannelType.TEXT, "848849465850462281").getMembers();
        List<Member> members = event.getGuildChannel().getMembers();
        for (Member x : members) System.out.println(x.getAsMention());
        for (int i = 0; i < event.getGuildChannel().getMembers().size(); i++) {
            System.out.println(i);
            System.out.println("interesting");
            System.out.println(members.get(i));
            if (members.get(i) == null) System.out.println("well ok fuck");
            curUser = members.get(i).getId();

            System.out.println(event.getGuild().getMemberById(curUser).getEffectiveName());
            System.out.println("a");
            if (DatabaseUtility.checkIfUserIsInCycle(curUser)) {
                System.out.println("b");
                if (!DatabaseUtility.checkUserDidWorkoutToday(curUser) && !DatabaseUtility.checkUserIsOnBreak(curUser)) {
                    DatabaseUtility.decrementUserFreeDays(curUser);
                    System.out.println("c");
                }
                if (DatabaseUtility.checkNegativeFreeDaysForUser(curUser)) {
                    kickedUsers.add(curUser);
                    System.out.println("d");
                    DatabaseUtility.removeUser(curUser);
                    event.getChannel().sendMessage(String.format("Hi %s, it looks like you missed your workout for more than two days this week. Goodbye.",event.getGuild().getMemberById(curUser).getAsMention()))
                    .queue();
                }
                System.out.println("e");
            }
        }
        System.out.println("OK I SEE");
        curUser = null;
        
        try {
            event.getChannel().sendMessage(generateDailyUpdate(event)).queue();
        } catch (Exception e) {e.printStackTrace();}
    }

    private void weeklyUpdate(SlashCommandEvent event) {
        String curUser;
        for (int i = 0; i < event.getGuild().getMemberCount(); i++) {
            curUser = event.getGuild().getMembers().get(i).getId();
            if (DatabaseUtility.checkIfUserIsInCycle(curUser)) {
                DatabaseUtility.refreshFreeDays(event.getGuild());
            }
        }
        DatabaseUtility.refreshFreeDays(event.getGuild());
        try {
            event.getChannel().sendMessage("@everyone It is a new week and rest days have been refreshed.\n Have a nice week and focus on your aim!").queue();
        } catch (Exception e) {e.printStackTrace();} 
    }

    public static void endOfCycleUpdate(SlashCommandEvent event) {
        try {
            event.getChannel().sendMessage("@everyone Cycle complete!").queue();
        } catch (Exception e) {e.printStackTrace();}

    }

    

    public void endBreakTimed(User user, int days) {
        Runnable endBreak = () -> DatabaseUtility.deactivateBreak(user);
        this.endBreakHandle = schedulerBreak.schedule(endBreak, days, SECONDS);
    }

    public void endBreakImmediate(User user) {
        // ends break for the specified user, and cancels the timed endbreak triggered by the break command response.
        if (!(this.endBreakHandle == null)) this.endBreakHandle.cancel(false);
        DatabaseUtility.deactivateBreak(user);
    }

    public void resumeUpdate() {
        //TODO
    }

    public void handleUpdates(SlashCommandEvent event) {
        
        this.weeklyUpdate = () -> weeklyUpdate(event);
        this.dailyUpdate = () -> dailyUpdate(event); 
        this.eocUpdate = () -> endOfCycleUpdate(event);
        
        this.dailyUpdateHandle = schedulerDaily.scheduleAtFixedRate(dailyUpdate, 10, 10, SECONDS);
        this.weeklyUpdateHandle = schedulerWeekly.scheduleAtFixedRate(weeklyUpdate, 71, 70, SECONDS);
        this.endOfCycleUpdateHandle = schedulerEOC.schedule(eocUpdate, DatabaseUtility.getCycleDurationInDays(), SECONDS);

        this.stopDailyUpdates = () -> dailyUpdateHandle.cancel(false);
        this.stopWeeklyUpdates = () -> weeklyUpdateHandle.cancel(false);
        //this.stopEOCUpdate = () -> endOfCycleUpdateHandle.cancel(false);

        int days = DatabaseUtility.getCycleDurationInDays();
        TimeUnit TIME_UNIT = SECONDS;
        schedulerDaily.schedule(this.stopDailyUpdates, days, TIME_UNIT);
        schedulerWeekly.schedule(this.stopWeeklyUpdates, days, TIME_UNIT);
        //schedulerEOC.schedule(this.stopEOCUpdate, days, TIME_UNIT);

        System.out.println("hmm OKK OKOKOKO");
        //event.getGuild().getMembers().forEach((x) -> DatabaseUtility.deactivateBreak(x.getUser()));
    }

    public void cancelTimedEvents() {
        this.dailyUpdateHandle.cancel(false); 
        this.endOfCycleUpdateHandle.cancel(false);
    }

}
