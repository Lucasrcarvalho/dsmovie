package com.devsuperior.dsmovie.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.Movie;
import com.devsuperior.dsmovie.entities.Score;
import com.devsuperior.dsmovie.entities.User;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.repositories.UserRepository;

@Service
public class ScoreService {
	
	@Autowired
	private MovieRepository movieRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ScoreRepository scoreRepository;
	
	@Transactional
	public MovieDTO saveScore(ScoreDTO dto) {
		
		/*Salvando o usuário através do email*/
		User user = userRepository.findByEmail(dto.getEmail());		
		if (user == null) {
			user = new User();
			user.setEmail(dto.getEmail());
			user = userRepository.saveAndFlush(user);
		}
		
		/*Buscando o filme o qual o usuário está*/
		Movie movie = movieRepository.findById(dto.getMovieId()).get();
		
		/*Salvando o score do filme, feito pelo usuário*/
		Score score = new Score();
		score.setMovie(movie);
		score.setUser(user);
		score.setValue(dto.getScore());		
		score = scoreRepository.saveAndFlush(score);
		
		/*Calcula a soma de todas as notas dadas a um filme*/
		double sum = 0.0;
		for (Score s : movie.getScores()) {
			sum += s.getValue();
		}
		
		/*Calcula a média das notas*/
		double avg = sum/movie.getScores().size();
		
		/*Setando os calculos nos atributos*/
		movie.setScore(avg);
		movie.setCount(movie.getScores().size());
		
		/*Salvando o filme com todas as informações*/
		movie = movieRepository.save(movie);
		
		return new MovieDTO(movie);
	}
}