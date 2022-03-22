
package FitBot;

import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.StackWalker.Option;
import java.util.Arrays;
import java.util.Scanner;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import java.io.IOException;
public class Bot extends ListenerAdapter
{

    public static SlashResponseHandler handleSlash = new SlashResponseHandler();
    
    public static void main(String[] args) throws LoginException
    {
    	String TOKEN = null;
    	Scanner tokenScanner = new Scanner(System.in);
    	
    	System.out.println("Enter auth token: ");
    	TOKEN = tokenScanner.nextLine();
    	
        
        final String ALIEN_TEST_SERVER_GUILD_ID = "848849465850462281";
        // We only need 2 intents in this bot. We only respond to messages in guilds and private channels.
        // All other events will be disabled.
        JDA jda  = JDABuilder.createLight(TOKEN, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
            .enableIntents(GatewayIntent.GUILD_MEMBERS)
            .setMemberCachePolicy(MemberCachePolicy.ALL)
            .addEventListeners(new Bot())
            .build();
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //jda.upsertCommand("track", "track a workout").queue(); // This can take up to 1 hour to show up in the client
        jda.getGuildById(ALIEN_TEST_SERVER_GUILD_ID).upsertCommand("track", "track a workout")
            .addOption(OptionType.STRING, "description", "briefly describe your workout")
            .queue(); // WAIT TILL DISCORD API IS UPDATED TO SUPPORT ATTACHMENTS IN SLASH COMMAND OPTIONS
        jda.getGuildById(ALIEN_TEST_SERVER_GUILD_ID).upsertCommand("break", "take a break if you need time off. e.g. feeling ill or overloaded with work")
            .addOption(OptionType.STRING, "reason", "What is your reason for taking time off?")
            .addOption(OptionType.INTEGER, "days", "How many days do you plan to take off? (you may end your time off early)")
            .queue(); 
        jda.getGuildById(ALIEN_TEST_SERVER_GUILD_ID).upsertCommand("end-break", "end your break and get back to working out")
            .queue(); 
        jda.getGuildById(ALIEN_TEST_SERVER_GUILD_ID).upsertCommand("quit", "quit the server (potentially irreversible)")
            .addOption(OptionType.STRING, "security", "Are you sure (yes/no)?")
            .queue(); 
        jda.getGuildById(ALIEN_TEST_SERVER_GUILD_ID).upsertCommand("suggestion", "provide an improvement suggestion for me")
            .addOption(OptionType.STRING, "suggestion", "how should I improve?")
            .queue();
        jda.getGuildById(ALIEN_TEST_SERVER_GUILD_ID).upsertCommand("set-aim", "what is your aim for this cycle?")
            .addOption(OptionType.STRING, "aim", "describe your aim")
            .queue();
        jda.getGuildById(ALIEN_TEST_SERVER_GUILD_ID).upsertCommand("get-report", "get a report on progress across cycles")
            .queue();
        jda.getGuildById(ALIEN_TEST_SERVER_GUILD_ID).upsertCommand("start-cycle", "Start a cycle!")
            .addOption(OptionType.STRING, "end-date", "format: YYYY-MM-DD (note: SYD date/time!)")
            .queue();
        jda.getGuildById(ALIEN_TEST_SERVER_GUILD_ID).upsertCommand("end-cycle", "end cycle early?")
            .addOption(OptionType.STRING, "hmm", "are you sure? (y/n)")
            .queue();
        jda.getGuildById(ALIEN_TEST_SERVER_GUILD_ID).upsertCommand("check-cycle-status", "Check if a cycle is currently running")
            .queue();
        jda.getGuildById(ALIEN_TEST_SERVER_GUILD_ID).upsertCommand("reflection", "add a reflection based on your aim for the current cycle")
            .addOption(OptionType.STRING, "reflection", "write a reflection")
            .queue();
        jda.getGuildById(ALIEN_TEST_SERVER_GUILD_ID).upsertCommand("register", "register for the current cycle")
            .queue();
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event)
    {
        if (event.getName().equals("break")) handleSlash.breakResponse(event); // ok for the time
        else if (event.getName().equals("track")) handleSlash.trackResponse(event); // seems ok
        else if (event.getName().equals("end-break")) handleSlash.endBreakResponse(event); // testing
        else if (event.getName().equals("quit")) handleSlash.quitResponse(event); //testing
        else if (event.getName().equals("suggestion")) handleSlash.suggestionResponse(event); //testing
        else if (event.getName().equals("set-aim"))  handleSlash.setAimResponse(event);//testing
        else if (event.getName().equals("reflection"))  handleSlash.reflectionResponse(event);//testing
        else if (event.getName().equals("get-report")) handleSlash.getReportResponse(event); // ok1
        else if (event.getName().equals("start-cycle")) handleSlash.startCycleResponse(event);//testing
        else if (event.getName().equals("check-cycle-status")) handleSlash.checkCycleStatusResponse(event);//testing
        else if (event.getName().equals("end-cycle")) handleSlash.endCycleResponse(event);//testing
        else if (event.getName().equals("register")) handleSlash.registerResponse(event);//testing
    }
    
    /* @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        Message msg = event.getMessage();
        
        User user = event.getAuthor();
        Guild server = event.getGuild();
        //System.out.println(msg.getContentDisplay());
        //System.out.println(msg.getContentRaw().matches("(.*)/track(.*)"));
        if (msg.getContentRaw().matches("(.*)/track(.*)") && !msg.getAuthor().isBot())
        {
            
            //System.out.println(msg.getAuthor());
            //System.out.println("ok");
            MessageChannel channel = event.getChannel();
            //System.out.println(channel);
            //long time = System.currentTimeMillis();
            channel.addReactionById(event.getMessageId(), "minn:245267426227388416");
            channel.sendMessage(String.format("Hi %s, your workout has been tracked.",user.getName())).complete();
            //System.out.println("A");
            //System.out.println(server.getSelfMember().hasPermission(Permission.MESSAGE_WRITE));
        }
    }  */


    
}
