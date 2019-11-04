package com.skilldistillery.filmquery.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public class DatabaseAccessorObject implements DatabaseAccessor {

	private static final String URL = "jdbc:mysql://localhost:3306/sdvid?useSSL=false";

	public DatabaseAccessorObject() throws ClassNotFoundException {
		 try {
		      Class.forName("com.mysql.jdbc.Driver");
		    }
		    catch (ClassNotFoundException e) {
		      System.err.println("Error loading driver.");
		      e.printStackTrace();
		    }
	}
	
	@Override
	public Film findFilmById(int filmId) {
		String user = "student";
		String pass = "student";
		Film film = null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL, user, pass);
			String sqltext = "select * from film join language where film.id = ?;";

			PreparedStatement stmt = conn.prepareStatement(sqltext);
			stmt.setInt(1, filmId);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				if (rs.getString("title") == null) {
					System.out.println("There is no movie at this ID.");
				} else {
					film = new Film(); // Create the object
					// Here is our mapping of query columns to our object fields:
					film.setId(rs.getInt("film.id"));
					film.setTitle(rs.getString("title"));
					film.setDescription(rs.getString("description"));
					film.setReleaseYear(rs.getInt("release_year"));
					film.setLanguageId(rs.getInt("language_id"));
					film.setRentalDuration(rs.getInt("rental_duration"));
					film.setRentalRate(rs.getDouble("rental_rate"));
					film.setLength(rs.getInt("length"));
					film.setReplacementCost(rs.getDouble("replacement_cost"));
					film.setRating(rs.getString("rating"));
					film.setSpecialFeatures(rs.getString("special_features"));
					film.setActors(findActorsByFilmId(filmId));
				}
			}

			rs.close();
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return film;

	}

	public Actor findActorById(int actorId) {
		String user = "student";
		String pass = "student";
		Actor actor = null;

		try {
			Connection conn = DriverManager.getConnection(URL, user, pass);
			String sql = "SELECT actor.id, actor.first_name, actor.last_name FROM actor WHERE actor.id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, actorId);
			ResultSet actorResult = stmt.executeQuery();

			if (actorResult.next()) {
				if (actorResult.getString("actor.first_name") == null) {
					System.out.println("There is no actor at this ID.");
				} else {
					if (actorResult.next()) {
						actor = new Actor(); // Create the object
						// Here is our mapping of query columns to our object fields:
						actor.setId(actorResult.getInt("actor.id"));
						actor.setFirstName(actorResult.getString("actor.first_name"));
						actor.setLastName(actorResult.getString("actor.last_name"));
						actor.setFilms(findFilmsByActorId(actorResult.getInt("actor_film.actor_id")));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return actor;
	}

	public List<Film> findFilmsByActorId(int actorId) {
		String user = "student";
		String pass = "student";
		List<Film> films = new ArrayList<>();
		try {
			Connection conn = DriverManager.getConnection(URL, user, pass);
			String sql = "SELECT film.id, film.title, film.description, film.release_year, film.language_id, film.rental_duration, ";
			sql += " film.rental_rate, film.length, film.replacement_cost, film.rating, film.special_features"
					+ " FROM film JOIN film_actor ON film.id = film_actor.film_id join language on film.language_id = language.id"
					+ " WHERE film_actor.actor_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, actorId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int filmId = rs.getInt("film.id");
				String title = rs.getString("film.title");
				String desc = rs.getString("film.description");
				short releaseYear = rs.getShort("film.release_year");
				int langId = rs.getInt("film.language_id");
				int rentDur = rs.getInt("film.rental_duration");
				double rate = rs.getDouble("film.rental_rate");
				int length = rs.getInt("film.length");
				double repCost = rs.getDouble("film.replacement_cost");
				String rating = rs.getString("film.rating");
				String features = rs.getString("film.special_features");
				List<Actor> actors = findActorsByFilmId(filmId);
				Film film = new Film(filmId, title, desc, releaseYear, langId, rentDur, rate, length, repCost, rating,
						features, actors);
				films.add(film);
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return films;
	}

	public List<Actor> findActorsByFilmId(int filmId) {
		String user = "student";
		String pass = "student";
		List<Actor> actors = new ArrayList<>();
		try {
			Connection conn = DriverManager.getConnection(URL, user, pass);
			String sql = "SELECT actor.id, actor.first_name, actor.last_name FROM actor JOIN film_actor ON actor.id = film_actor.actor_id "
					+ " WHERE film_actor.film_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, filmId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("actor.id");
				String firstName = rs.getString("actor.first_name");
				String lastName = rs.getString("actor.last_name");
//				List<Film> films = findFilmsByActorId(id);
				Actor actor = new Actor(id, firstName, lastName);
				actors.add(actor);
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return actors;
	}

	@Override
	public List<Film> findFilmByKeyword(String keyword) {
		String user = "student";
		String pass = "student";
		List<Film> films = new ArrayList<Film>();
		;
		Film film = null;

		try {
			Connection conn = DriverManager.getConnection(URL, user, pass);
			String sqltext;
			sqltext = "select * from film WHERE film.title like ? or film.description like ?";

			PreparedStatement stmt = conn.prepareStatement(sqltext);
			stmt.setString(1, "%" + keyword + "%");
			stmt.setString(2, "%" + keyword + "%");
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				if (rs.getString("film.title") == null) {
					System.out.println("No films match this keyword.");
				} else {
					film = new Film(); // Create the object
					// Here is our mapping of query columns to our object fields:
					film.setId(rs.getInt("film.id"));
					film.setTitle(rs.getString("film.title"));
					film.setDescription(rs.getString("film.description"));
					film.setReleaseYear(rs.getInt("film.release_year"));
					film.setLanguageId(rs.getInt("film.language_id"));
					film.setRentalDuration(rs.getInt("film.rental_duration"));
					film.setRentalRate(rs.getDouble("film.rental_rate"));
					film.setLength(rs.getInt("film.length"));
					film.setReplacementCost(rs.getDouble("film.replacement_cost"));
					film.setRating(rs.getString("film.rating"));
					film.setSpecialFeatures(rs.getString("film.special_features"));
					film.setActors(findActorsByFilmId(rs.getInt("film.id")));
					films.add(film);
				}
			}

			rs.close();
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return films;
	}

	public String getLanguage(int id) {
		String user = "student";
		String pass = "student";
		String language = null;

		try {
			Connection conn = DriverManager.getConnection(URL, user, pass);
			String sqltext;
			sqltext = "select * from film JOIN language ON language.id = film.language_id where film.language_id = ?";

			PreparedStatement stmt = conn.prepareStatement(sqltext);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				language = rs.getString("language.name");
			}

			rs.close();
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return language;
	}
}
