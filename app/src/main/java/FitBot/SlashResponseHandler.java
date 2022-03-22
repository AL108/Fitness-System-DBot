package FitBot;

import javax.xml.crypto.Data;

import org.checkerframework.checker.regex.qual.Regex;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class SlashResponseHandler {
    TimedEventsHandler x = new TimedEventsHandler();

    public void breakResponse(SlashCommandEvent event) {
        if (!DatabaseUtility.checkUserExists(event.getUser())) DatabaseUtility.addUser(event.getUser());
        if (!DatabaseUtility.checkIfUserIsInCycle(event.getUser().getId())) {
            try {
                event.reply(String.format("Hi %s, you must register for the current cycle before using any cycle commands. Please register and try again.", event.getUser().getAsMention())).queue();
            } catch (Exception e) {e.printStackTrace();}
            return;
        }
        if (DatabaseUtility.checkUserIsOnBreak(event.getUser().getId())) {
            String msg = String.format("Hi %s, you are already on break. Your break ends in [to be implemented] days. If you would like to end your break early, you may use the /endbreak command.", event.getUser().getAsMention());
            try {
                event.reply(msg).queue();
            } catch (Exception e) {e.printStackTrace();}
            return;
        }
        String msg = String.format("Hi %s, your time off has now started. You have %d days remaining. If you would like to end your break early use the /endbreak command.",
            event.getUser().getAsMention(),event.getOption("days").getAsLong());
        try {
            event.reply(msg)
            .queue();
        } catch (Exception e) {e.printStackTrace();}
        DatabaseUtility.activateBreak(event.getUser());
        x.endBreakTimed(event.getUser(), Integer.valueOf(event.getOption("days").getAsString()));
    }

    public void trackResponse(SlashCommandEvent event) {
        if (!DatabaseUtility.checkIfUserIsInCycle(event.getUser().getId())) {
            try {
                event.reply(String.format("Hi %s, you must register for the current cycle before using any cycle commands. Please register and try again.", event.getUser().getAsMention())).queue();
            } catch (Exception e) {e.printStackTrace();}
            return;
        }
        if (!DatabaseUtility.checkUserExists(event.getUser())) DatabaseUtility.addUser(event.getUser());
        if (!DatabaseUtility.currentlyInCycle()) {
            try {
                event.reply(String.format("Hi %s, workouts can only be tracked while a cycle is active. A cycle is not currently active.",event.getUser().getAsMention())).setEphemeral(true).queue();
            } catch (Exception e) {e.printStackTrace();}
            return;
        }

        if (event.getOption("description") == null) {
            try {
                event.reply(String.format("Hi %s, to track a workout, a description is required. Please try again.",event.getUser().getAsMention())).setEphemeral(true).queue();
            } catch (Exception e) {e.printStackTrace();}
            return;
        }

        DatabaseUtility.addWorkout(event.getUser(), event.getOption("description").getAsString());
        try {
            event.reply(String.format("Hi %s, your workout has been tracked.\nWorkout description: %s",event.getUser().getAsMention(), event.getOption("description").getAsString()))
            .queue();
        } catch (Exception e) {e.printStackTrace();}
    }

    public void endBreakResponse(SlashCommandEvent event) {
        if (!DatabaseUtility.checkIfUserIsInCycle(event.getUser().getId())) {
            try {
                event.reply(String.format("Hi %s, you must register for the current cycle before using any cycle commands. Please register and try again.", event.getUser().getAsMention())).queue();
            } catch (Exception e) {e.printStackTrace();}
            return;
        }
        if (!DatabaseUtility.checkUserExists(event.getUser())) DatabaseUtility.addUser(event.getUser());
        if (!DatabaseUtility.checkUserIsOnBreak(event.getUser().getId())) {
            try {
                event.reply(String.format("Hi %s, you are not currently on break.",event.getUser().getAsMention())).queue();
            } catch (Exception e) {e.printStackTrace();}
            return;
        }
        x.endBreakImmediate(event.getUser());
        try {
            event.reply(String.format("Hi %s, you have ended your break.",event.getUser().getAsMention())).queue();
        } catch (Exception e) {e.printStackTrace();}
    }

    public void quitResponse(SlashCommandEvent event) {
        if (!DatabaseUtility.checkUserExists(event.getUser())) DatabaseUtility.addUser(event.getUser());
        DatabaseUtility.activateBreak(event.getUser());
        try {
            event.reply(String.format("I haven't got the permission to kick server members, so feel free to hop off. Goodbye, %s",event.getUser().getAsMention())).queue();
        } catch (Exception e) {e.printStackTrace();}
    }

    public void suggestionResponse(SlashCommandEvent event) {
        
        if (!DatabaseUtility.checkUserExists(event.getUser())) DatabaseUtility.addUser(event.getUser());
        if (event.getOption("suggestion") == null) {
            try {
                event.reply(String.format("Hi %s, it seems that a suggestion has not been entered. Please try again.",event.getUser().getAsMention())).setEphemeral(true).queue();
            } catch (Exception e) {e.printStackTrace();}
            return;
        }
        DatabaseUtility.addSuggestion(event.getUser().getId(), event.getOption("suggestion").getAsString());
        try {
            event.reply(String.format("Thanks %s, I will take your suggestion into consideration.\nSuggestion: %s",event.getUser().getAsMention(), event.getOption("suggestion").getAsString())).queue();
        } catch (Exception e) {e.printStackTrace();}
    }

    public void reflectionResponse(SlashCommandEvent event) {
        if (!DatabaseUtility.checkIfUserIsInCycle(event.getUser().getId())) {
            try {
                event.reply(String.format("Hi %s, you must register for the current cycle before using any cycle commands. Please register and try again.", event.getUser().getAsMention())).queue();
            } catch (Exception e) {e.printStackTrace();}
            return;
        }
        if (!DatabaseUtility.checkUserExists(event.getUser())) DatabaseUtility.addUser(event.getUser());
        if (!DatabaseUtility.currentlyInCycle()) {
            try {
                event.reply(String.format("Hi %s, you can only set an reflection when a cycle is running. Currently there is no cycle running.",event.getUser().getAsMention()))
                .setEphemeral(true)
                .queue();
            } catch (Exception e) {e.printStackTrace();}
            return;
        }
        //if (!DatabaseUtility.checkIfUserIsInCycle(event.getUser().getId())) DatabaseUtility.addUserToCycle(event.getUser().getId());
        if (event.getOption("reflection") == null) {
            try {
                event.reply(String.format("Hi %s, it seems that a reflection has not been entered. Please try again.",event.getUser().getAsMention())).setEphemeral(true).queue();
            } catch (Exception e) {e.printStackTrace();}
            return;
        }
        DatabaseUtility.setUserReflectionForCurrentCycle(event.getOption("reflection").getAsString(), event.getUser().getId());
        try {
            event.reply(String.format("Hi %s, your reflection has been set!\nReflection: %s\n\n",event.getUser().getAsMention(),event.getOption("reflection").getAsString(),event.getUser().getAsMention())).queue();
        } catch (Exception e) {e.printStackTrace();}
    }

    public void setAimResponse(SlashCommandEvent event)  {
        if (!DatabaseUtility.checkIfUserIsInCycle(event.getUser().getId())) {
            try {
                event.reply(String.format("Hi %s, you must register for the current cycle before using any cycle commands. Please register and try again.", event.getUser().getAsMention())).queue();
            } catch (Exception e) {e.printStackTrace();}
            return;
        }
        if (!DatabaseUtility.checkUserExists(event.getUser())) DatabaseUtility.addUser(event.getUser());
        
        if (!DatabaseUtility.currentlyInCycle()) {
            try {
                event.reply(String.format("Hi %s, you can only set an aim when a cycle is running. Currently there is no cycle running.",event.getUser().getAsMention()))
                .setEphemeral(true)
                .queue();
            } catch (Exception e) {e.printStackTrace();}
            return;
        }
        //if (!DatabaseUtility.checkIfUserIsInCycle(event.getUser().getId())) DatabaseUtility.addUserToCycle(event.getUser().getId());
        if (event.getOption("aim") == null) {
            try {
                event.reply(String.format("Hi %s, it seems that an aim has not been entered. Please try again.",event.getUser().getAsMention())).setEphemeral(true).queue();
            } catch (Exception e) {e.printStackTrace();}
            return;
        }
        DatabaseUtility.setUserAimForCurrentCycle(event.getOption("aim").getAsString(), event.getUser().getId());
        try {
            event.reply(String.format("Hi %s, your aim has been set!\nAim: %s\n\nGood Luck %s!!!",event.getUser().getAsMention(),event.getOption("aim").getAsString(),event.getUser().getAsMention())).queue();
        } catch (Exception e) {e.printStackTrace();}
    }

    public void getReportResponse(SlashCommandEvent event) {
        if (!DatabaseUtility.checkUserExists(event.getUser())) DatabaseUtility.addUser(event.getUser());
        try {
            event.reply(String.format("Hi %s, sorry I can't make a report for you right now, I am eating lunch. I should be done with my lunch by next cycle.",event.getUser().getAsMention())).queue();
            
        } catch (Exception e) {e.printStackTrace();}
        
    }

    public void startCycleResponse(SlashCommandEvent event) {
        if (!DatabaseUtility.checkUserExists(event.getUser())) DatabaseUtility.addUser(event.getUser());
        if (DatabaseUtility.currentlyInCycle()) {
            try {
                event.reply(String.format("Hi %s, there is already a cycle running.",event.getUser().getAsMention()))
                .setEphemeral(true)
                .queue();
            } catch (Exception e) {e.printStackTrace();}
        } else if (event.getOption("end-date") == null) {
            try {
                event.reply(String.format("Hi %s, to start a cycle, you must specify an end_date. Please try again.",event.getUser().getAsMention()))
                .setEphemeral(true)
                .queue();
            } catch (Exception e) {e.printStackTrace();}
        } else {
            System.out.println("A");
            
            System.out.println(event.getOption("end-date").getAsString());
            
            if (!event.getOption("end-date").getAsString().matches("^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$")) {
                System.out.println("B");
                try {
                    event.reply("Invalid date format entered. Please try again with the specified format.").queue();;
                } catch (Exception e) {e.printStackTrace();}
            } else {
                DatabaseUtility.startCycle(event.getOption("end-date").getAsString());
                System.out.println("C");
                this.x.handleUpdates(event);
                System.out.println("D");
                try {
                    event.getChannel().sendMessage(String.format("Hi %s, you have started the cycle. Good luck @everyone, this cycle ends on %s",event.getUser().getAsMention(),event.getOption("end-date").getAsString()))
                    .queue();
                } catch (Exception e) {e.printStackTrace();}
            }
        }
    }

    public void checkCycleStatusResponse(SlashCommandEvent event) {
        if (!DatabaseUtility.checkUserExists(event.getUser())) DatabaseUtility.addUser(event.getUser());
        if (!DatabaseUtility.currentlyInCycle()) {
            try {
                event.reply(String.format("Hi %s, there is no cycle running.",event.getUser().getAsMention())).queue();
            } catch (Exception e) {e.printStackTrace();}
        } else {
            try {
                event.getChannel().sendMessage(String.format("Hi %s, a cycle is running.",event.getUser().getAsMention())).queue();
            } catch (Exception e) {e.printStackTrace();}
        }
    }

    public void registerResponse(SlashCommandEvent event) {
        if (!DatabaseUtility.checkUserExists(event.getUser())) DatabaseUtility.addUser(event.getUser());
        if (DatabaseUtility.checkIfUserIsInCycle(event.getUser().getId())) {
            try {
                event.reply(String.format("Hi %s, you have already been registered for the current cycle.",event.getUser().getAsMention()))
                .queue();
            } catch (Exception e) {e.printStackTrace();}
            return;
        }
        DatabaseUtility.addUserToCycle(event.getUser().getId());
        try {
            event.reply(String.format("Hi %s, your have successfully registered for the current cycle.",event.getUser().getAsMention()))
            .queue();
        } catch (Exception e) {e.printStackTrace();}
    }

    public void endCycleResponse(SlashCommandEvent event) {
        if (!DatabaseUtility.checkUserExists(event.getUser())) DatabaseUtility.addUser(event.getUser());
        if (event.getOption("hmm") == null) {
            try {
                event.reply(String.format("Hi, %s, you must specify whether you are sure or not. Please try again.",event.getUser().getAsMention()))
                .setEphemeral(true)
                .queue();
            } catch (Exception e) {e.printStackTrace();}
            return;
        } 

        if (!event.getOption("hmm").getAsString().equals("y")) {
            try {
                event.reply(String.format("ok, careful lol",event.getUser().getAsMention()))
                .setEphemeral(true)
                .queue();
            } catch (Exception e) {e.printStackTrace();}
            return;
        }
 
        if (!DatabaseUtility.currentlyInCycle()) {
            try {
                event.reply(String.format("Hi, %s, there is already no cycle running.",event.getUser().getAsMention()))
                .setEphemeral(true)
                .queue();
            } catch (Exception e) {e.printStackTrace();}
        } else {
            DatabaseUtility.endCurrentCycle();
            try {
                event.reply(String.format("Hi, %s, the cycle has been discontinued.",event.getUser().getAsMention())).queue();
            } catch (Exception e) {e.printStackTrace();}
            TimedEventsHandler.endOfCycleUpdate(event);
            this.x.cancelTimedEvents();
        }

        
    }
    
}
