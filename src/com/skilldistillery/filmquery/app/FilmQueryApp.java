package com.skilldistillery.filmquery.app;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.skilldistillery.filmquery.database.DatabaseAccessor;
import com.skilldistillery.filmquery.database.DatabaseAccessorObject;
import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public class FilmQueryApp {

	DatabaseAccessor db = new DatabaseAccessorObject();

	public FilmQueryApp() throws ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
	}

	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		FilmQueryApp app = new FilmQueryApp();
//    app.test();
		app.launch();
	}

	private void test() throws SQLException {
		Film film = db.findFilmById(1);
		System.out.println(film);
	}

	private void launch() {
		Scanner input = new Scanner(System.in);

		startUserInterface(input);

		input.close();
	}

	private void startUserInterface(Scanner input) {
		boolean again = true;
		do {
			System.out.println("Film Database Menu:");
			System.out.println("1) Look up film by ID");
			System.out.println("2) Look up film by search keyword");
			System.out.println("3) Exit application");
			int selection = input.nextInt();
			input.nextLine();
			switch (selection) {
				case 1: {
					System.out.println("Enter film ID:");
					int id = input.nextInt();

					System.out.println(db.findFilmById(id).toString());
					System.out.print("Language: ");
					System.out.println(db.getLanguage(db.findFilmById(id).getLanguageId()));
					List<Actor> actors = new ArrayList<>();
					actors = db.findFilmById(id).getActors();
					System.out.println("Actors: ");
					for (Actor actor : actors) {
						System.out.println(actor);
					}
					System.out.println();
					break;
				}
	
				case 2: {
					boolean keepGoing = true;
					List<Film> films = new ArrayList<>();
					do {
						System.out.println("Enter search keyword:");
						String searchTerm = input.nextLine();
						films = db.findFilmByKeyword(searchTerm);

						if (films.size() == 0) {
							System.out.println("No matches for that keyword.");
						}else {
							for (Film film : films) {
								System.out.println(film.toString());
								System.out.print("Language: ");
								System.out.println(db.getLanguage(film.getLanguageId()));
								List<Actor> actors = new ArrayList<>();
								actors = film.getActors();
								System.out.println("Actors: ");
								for (Actor actor : actors) {
									System.out.println(actor);
								}
								System.out.println();
							}
							keepGoing = false;
						}
					} while(keepGoing);	
					break;
				}
	
				case 3: {
					System.out.println("Goodbye.");
					again = false;
					break;
				}
	
				default: {
					System.out.println("Invalid input.");
				}

			}
		} while (again);
	}
}
