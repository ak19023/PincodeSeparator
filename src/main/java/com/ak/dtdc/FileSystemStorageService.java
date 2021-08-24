package com.ak.dtdc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements StorageService {

	private final Path rootLocation = Paths.get(FileUploadController.basePath);
	private String filePath;

	@Override
	public void store(MultipartFile file) {
		try {
			if (file.isEmpty()) {
				System.out.println("Cannot store empty files");
			}
			Path destinationFile = this.rootLocation.resolve(
					Paths.get(file.getOriginalFilename()))
					.normalize().toAbsolutePath();
			if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
				// This is a security check
				System.out.println("Cannot store files outside directory");
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile,
					StandardCopyOption.REPLACE_EXISTING);
				filePath = FileUploadController.basePath + File.separator + file.getOriginalFilename();
			}
		}
		catch (IOException e) {
			System.out.println("Failed to store files");
		}
	}
	
	@Override
	public void separatePinsAndPhones() throws Exception {
			separator.separate(filePath);
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1)
				.filter(path -> !path.equals(this.rootLocation))
				.map(this.rootLocation::relativize);
		}
		catch (IOException e) {
			System.out.println("Failed to read stored files");
			return null;
		}

	}

	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new Exception(
						"Could not read file: " + filename);
			}
		}
		catch (Exception e) {
			return null;
		}
	}

	/*
	 * Deletes all the files previously existed in the folder
	 */
	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
		}
		catch (IOException e) {
			System.out.println("Could not initialize storage");
		}
	}
	
}
