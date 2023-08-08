package com.plzgraduate.myongjigraduatebe.fixture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

public class LectureFixture {
	private static final Map<String, Lecture> mockLectureMap;

	static {
		mockLectureMap = new HashMap<>();
		setUpMockLectureMap();
	}

	public static Map<String, Lecture> getMockLectureMap() {
		return mockLectureMap;
	}

	private static void setUpMockLectureMap() {
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		Resource resource = resourceLoader.getResource("/lecture.csv");

		try (InputStream inputStream = resource.getInputStream();
			 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

			String line;
			while ((line = reader.readLine()) != null) {
				String[] columns = line.split(", ");
				String lectureCode = columns[0];
				String name = columns[1];
				int credit = Integer.parseInt(columns[2]);
				int isRevoked = Integer.parseInt(columns[3]);
				String duplicateCode = columns[4];

				Lecture lecture = Lecture.of(lectureCode, name, credit, isRevoked, duplicateCode);
				mockLectureMap.put(lectureCode, lecture);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
