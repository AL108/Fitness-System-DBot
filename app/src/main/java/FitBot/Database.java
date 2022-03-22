package FitBot;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
	private String dbUrl;
	private String dbPassword;
	private String dbUsername;

	public Database(String dbUrl, String dbUsername, String dbPassword) {
		this.dbUrl = dbUrl;
		this.dbPassword = dbPassword;
		this.dbUsername = dbUsername;
	}


	public Boolean sql_update(String query) {
		/*desc todo*/
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public String sql_getString(String query, String column) {
		/*desc todo*/
		String output = null;
		Connection conn = null;
		String sql = query;
		try {
			conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
			Statement stmt = conn.createStatement();
			ResultSet result = stmt.executeQuery(sql);
			if (result.next()) {
				output = result.getString(column);
			}
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return output;
	}

	public List<String> sql_getStrings(String query, String column) {
		/*desc todo*/
		String output = null;
		Connection conn = null;
		String sql = query;
		List<String> res = new ArrayList<>(0);
		try {
			conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
			Statement stmt = conn.createStatement();
			ResultSet result = stmt.executeQuery(sql);
			while (result.next()) {
				output = result.getString(column);
				res.add(output);
			}
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			return null;
		}
		return res;
	}


	public Boolean sql_getBoolean(String query, String column) {
		/*desc todo*/
		Connection conn = null;
		Boolean output = false;
		String sql = query;
		try {
			conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
			Statement stmt = conn.createStatement();
			ResultSet result = stmt.executeQuery(sql);
			if (result.next()) {
				output = result.getBoolean(column);
			} else return null;
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return output;
	}


	public Double sql_getDouble(String query, String column) {
		Connection conn = null;
		Double output = 0.0;
		String sql = query;
		try {
			conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
			Statement stmt = conn.createStatement();
			ResultSet result = stmt.executeQuery(sql);
			if (result.next()) {
				output = result.getDouble(column);
			} else return null;
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return output;
	}


	public Integer sql_getInt(String query, String column) {
		Connection conn = null;
		Integer output = 0;
		String sql = query;
		try {
			conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
			Statement stmt = conn.createStatement();
			ResultSet result = stmt.executeQuery(sql);
			if (result.next()) {
				output = result.getInt(column);
			} else return null;
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return output;
	}

	public String sql_getDate(String query, String column) {
		Connection conn = null;
		String output;
		String sql = query;
		try {
			conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
			Statement stmt = conn.createStatement();
			ResultSet result = stmt.executeQuery(sql);
			if (result.next()) {
				output = result.getString(column);
			} else return null;
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			return null;
		}
		return output;
	}

}