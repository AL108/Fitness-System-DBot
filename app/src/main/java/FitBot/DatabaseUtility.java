package FitBot;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.base.Supplier;

import org.checkerframework.checker.units.qual.s;

import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;


public class DatabaseUtility {
    public static Database x = new Database("jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "fuck");
    
    public static Boolean startCycle(String endDate) {
        String query = String.format("insert into fitness_db.cycle values ('now', date '%s' + time 'now')", endDate);
        return x.sql_update(query);
    }

    public static boolean endCurrentCycle() {
        String query = String.format("update fitness_db.cycle set end_date = 'now' where end_date >= 'now';");
        Boolean querySuccess = x.sql_update(query);
        if (querySuccess == null) return false;
        return (querySuccess);
    }

    public static void addSuggestion(String userID, String suggestion) {
        String query = String.format("insert into fitness_db.Suggestion values('%s','%s')", userID, suggestion);
        x.sql_update(query);
    }   

    public static int getCycleDurationInDays() {
        String endDate = getCurrentCycleEndDate();
        String query = String.format("select date '%s' - 'now' as duration;", endDate);
        System.out.println(x.sql_getInt(query, "duration"));
        return x.sql_getInt(query, "duration");
    }

    public static String getCurrentCycleEndDate() {
        String query = String.format("select end_date from fitness_db.cycle where end_date > 'now';");
        return x.sql_getString(query, "end_date");
    }

    public static void setUserAimForCurrentCycle(String aim, String userID) {
        String query = String.format("update fitness_db.cycle_user set aim = '%s' where user_ = '%s' and start_timestamp = '%s';",aim,userID,getCycleStartTimestamp());
        x.sql_update(query);
    }

    public static void setUserReflectionForCurrentCycle(String reflection, String userID) {
        String query = String.format("update fitness_db.cycle_user set reflection = '%s' where user_ = '%s';",reflection,userID);
        x.sql_update(query);
    }

    public static boolean checkUserExists(User user) {
        String query = String.format("select * from fitness_db.user_ where discord_user_id = '%s';",user.getId());
        return (x.sql_getString(query, "discord_user_id") != null);
    }

    public static boolean checkUserIsOnBreak(String userID) {
        String query = String.format("select in_break from fitness_db.user_ where discord_user_id = '%s';",userID);
        boolean value = x.sql_getBoolean(query, "in_break");
        return value;
    }

    public static void activateBreak(User user) {
        String userID = user.getId();
        String query = String.format("update fitness_db.user_ set in_break = true where discord_user_id = '%s';",userID);
        x.sql_update(query);
    } 

    public static int getNumberOfRestDaysLeftForUser(String uid) {
        String query = String.format("select free_days_left_in_week from fitness_db.cycle_user where user_ = '%s' and start_timestamp = '%s';",uid,getCycleStartTimestamp());
        return x.sql_getInt(query, "free_days_left_in_week");

    }

    public static boolean checkUserOnBreak(String uid) {
        String query = String.format("select in_break from fitness_db.user_ where discord_user_id = '%s'",uid);
        return x.sql_getBoolean(query, "in_break");
    }

    public static List<String> getUserWorkoutsForToday(String userID) {
        String query = String.format("select evidence_description from fitness_db.workout where user_ = '%s' and timestamp > date 'today' - 1 + time 'now';",userID);
        return x.sql_getStrings(query, "evidence_description");
    }

    public static void removeUser(String userID) {
        String query = String.format("delete from fitness_db.user_ where discord_user_id = '%s';",userID);
        x.sql_update(query);
    }

    public static void deactivateBreak(User user) {
        String userID = user.getId();
        String query = String.format("update fitness_db.user_ set in_break = false where discord_user_id = '%s'",userID);
        x.sql_update(query);
    }

    public static void addUser(User user) {
        String query = String.format("insert into fitness_db.user_(discord_user_id) values ('%s')", user.getId());
        x.sql_update(query);
    }

    public static String getCycleUpdateTime() {
        // todo: test
        String query = "select end_date::time from fitness_db.cycle where end_date >= now();";
        return(x.sql_getString(query, "end_date"));
    }

    public static String getCycleStartTimestamp() {
        String query = "select start_date_ from fitness_db.cycle where end_date > 'now';";
        return x.sql_getString(query, "start_date_");
    }

    public static void addUserToCycle(String userID) {
        String query = String.format("insert into fitness_db.cycle_user(user_, start_timestamp) values('%s','%s');",userID,getCycleStartTimestamp());
        x.sql_update(query);
    }

    public static boolean checkUserDidWorkoutToday(String userID) {
        int number = 1;
        String getDailyNumQuery;
        if (checkAheadDailyUpdateTime()) {
            getDailyNumQuery = String.format("select user_ from fitness_db.workout where user_ = '%s' and timestamp_ > date 'today' + time '%s' and timestamp_ < 'now'",userID,getCycleUpdateTime());
        } else {
            getDailyNumQuery = String.format("select user_ from fitness_db.workout where user_ = '%s' and timestamp_ > date 'today' - 1 + time 'now' and timestamp_ < 'now'",userID,getCycleUpdateTime());
        }
        List<String> strs = x.sql_getStrings(getDailyNumQuery,"user_");
        if (strs == null) return false;
        else if (strs.size() == 0) return false;
        else return true;
    }

    public static void decrementUserFreeDays(String userID) {
        String query = String.format("update fitness_db.cycle_user set free_days_left_in_week = (select free_days_left_in_week from fitness_db.cycle_user where user_ = '%s' and start_timestamp = '%s') - 1 where user_ = '%s' and start_timestamp = '%s';",userID,getCycleStartTimestamp(),userID,getCycleStartTimestamp());
        x.sql_update(query);
    }

    //private static void generateRefreshQuery(String userID) {}

    private static void refreshFreeDaysForUser(String userID) {
        String query = String.format("update fitness_db.cycle_user set free_days_left_in_week = 2 where user_ = '%s';", userID);
        x.sql_update(query);
    }

    public static void refreshFreeDays(Guild server) {
        Consumer<String> refreshUserFreeDays = uid -> refreshFreeDaysForUser(uid);
        server.getMembers().forEach(uid -> refreshUserFreeDays.accept(uid.getUser().getId()));
    }

    public static boolean checkNegativeFreeDaysForUser(String userID) {
        String query = String.format("select free_days_left_in_week from fitness_db.cycle_user where user_ = '%s';",userID);
        int freeDays = x.sql_getInt(query, "free_days_left_in_week");
        System.out.println(String.format("number of free days: %s", freeDays));
        if (freeDays < 0) return true;
        return false;
    }
 
    public static boolean checkIfUserIsInCycle(String userID) {
        if (!currentlyInCycle()) return false;
        String query = String.format("select user_ from fitness_db.cycle_user where user_ = '%s' and start_timestamp = '%s';", userID,getCycleStartTimestamp());
        return (x.sql_getString(query, "user_") != null);
    }

    public static boolean currentlyInCycle() {
        String query = String.format("select end_date from fitness_db.cycle where end_date >= 'now';");
        return (x.sql_getString(query, "end_date") != null);
    }

    public static boolean checkAheadDailyUpdateTime() {
        String query = String.format("select time 'now' > '%s' as ahead_of_daily_update", getCycleUpdateTime());
        return x.sql_getBoolean(query, "ahead_of_daily_update");
    }

    public static void addWorkout(User user, String evidenceDescription) {
        int number = 1;
        String getDailyNumQuery;
        if (checkAheadDailyUpdateTime()) {
            getDailyNumQuery = String.format("select user_ from fitness_db.workout where user_ = '%s' and timestamp_ > date 'today' + time '%s' and timestamp_ < 'now'",user.getId(),getCycleUpdateTime());
        } else {
            getDailyNumQuery = String.format("select user_ from fitness_db.workout where user_ = '%s' and timestamp_ > date 'today' - 1 + time 'now' and timestamp_ < 'now'",user.getId(),getCycleUpdateTime());
        }
        
        Integer prevNumber =  x.sql_getStrings(getDailyNumQuery,"user_").size();
        if (prevNumber != null) {   
            number = prevNumber + 1;
        }

        String query = String.format("insert into fitness_db.workout values('now','%s','%s',%d)",user.getId(), evidenceDescription, number);
        x.sql_update(query);
    }
}
