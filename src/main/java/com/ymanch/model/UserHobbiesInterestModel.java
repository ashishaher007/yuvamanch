package com.ymanch.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserHobbiesInterestModel {
	@NotEmpty(message = "User hobbies should not be empty")
	private String userHobbies;

	@NotEmpty(message = "User hobbies music brand should not be empty")
	private String userHobbiesMusicBrands;

	@NotEmpty(message = "User hobbies tv shows should not be empty")
	private String userHobbiesTvShows;

	@NotEmpty(message = "User hobbies movies should not be empty")
	private String userHobbiesMovies;

	@NotEmpty(message = "User hobbies games should not be empty")
	private String userHobbiesGames;

}
