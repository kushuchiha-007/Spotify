package com.example.newproject;

import com.example.newproject.controller.SpotifyController;
import com.example.newproject.model.MongoArtist;
import com.example.newproject.sevice.SpotifyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SpotifyServiceTest {

	@Mock
	private SpotifyService SpotifyService;

	@InjectMocks
	private SpotifyController postController;

	@Test
	public void testGetArtists() throws IOException, InterruptedException {
		String id = "temp";
		MongoArtist artist = new MongoArtist();

		artist.setId(id);
		artist.setName("Arijit Singh");
		artist.setType("Singer");

		when(SpotifyService.getMongoArtists(id)).thenReturn(artist);

		MongoArtist result = postController.getMongoArtists(id);

		assertEquals(id, result.getId());
		assertEquals("Arijit Singh", result.getName());
		assertEquals("Singer", result.getType());

		verify(SpotifyService, times(1)).getMongoArtists(id);
	}

	@Test
	public void testMongoSearch() {
		List<MongoArtist> artistsList = new ArrayList<>();
		MongoArtist artist1 = new MongoArtist();
		artist1.setId("1");
		artist1.setName("Artist 1");
		artist1.setType("Type 1");
		artist1.setGenre(Arrays.asList("Pop", "Rock"));

		MongoArtist artist2 = new MongoArtist();
		artist2.setId("2");
		artist2.setName("Artist 2");
		artist2.setType("Type 2");
		artist2.setGenre(Arrays.asList("Jazz", "Rap"));

		artistsList.add(artist1);
		artistsList.add(artist2);

		when(SpotifyService.searchByGenre()).thenReturn(artistsList);

		List<MongoArtist> result = postController.searchByGenre();

		assertEquals(artistsList, result);

		verify(SpotifyService, times(1)).searchByGenre();
	}

}
