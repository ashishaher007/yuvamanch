package com.ymanch.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ymanch.controller.HomeController;
import com.ymanch.entity.Notification;
import com.ymanch.entity.Posts;
import com.ymanch.entity.User;
import com.ymanch.helper.Enums.NotificationStatus;
import com.ymanch.helper.Enums.NotificationType;
import com.ymanch.model.PostIndexPage;
import com.ymanch.repository.FriendRequestRepository;
import com.ymanch.repository.NotificationRepository;
import com.ymanch.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommonFunctions {

	@Value("${spring.profiles.active}")
	private String activeProfile;

	@Value("${UPLOAD_DIR}")
	private String UPLOAD_DIR;

	@Value("${UPLOAD_DIR_ORIGINAL_VIDEO_COMPRESSED}")
	private String VIDEO_DIR;

	@Value("${UPLOAD_DIR_VIDEO_THUMBNAIL}")
	private String THUMBNAIL_DIR;

	@Value("${FFMPEG_PATH}")
	private String ffmpegPath;

	@Value("${https.backend.server.url}")
	private String UPLOAD_PATH_URL;

	@Value("${UPLOAD_BASE_PATH}")
	private String POST_BASE_PATH;

//	@Value("${UPLOAD_COMPRESSED_VIDEO_BASE_PATH}")
//	private String COMP_VIDEO_BASE_PATH;

	@Value("${UPLOAD_THUMBNAIL_BASE_PATH}")
	private String THUMBNAIL_BASE_PATH;

	@Autowired
	private UserRepository useRepo;
	@Autowired
	private FriendRequestRepository friendRequestRepo;
	@Autowired
	private NotificationRepository notificationRepo;

	private static final Logger LOGGER = Logger.getLogger(CommonFunctions.class.getName());

	private RedisTemplate<String, Object> redisTemplate;
	private UserRepository userRepo;

	public CommonFunctions(RedisTemplate<String, Object> redisTemplate, UserRepository userRepo) {
		super();
		this.redisTemplate = redisTemplate;
		this.userRepo = userRepo;
	}

	// this function is used by more than one
	public void updatePostTimeAgo(List<PostIndexPage> indexData) {
		for (PostIndexPage postIndex : indexData) {
			String timeAgo = DateTimeUtil.convertToTimeAgo(postIndex.getPostCreatedAt());
			postIndex.setPostUploadedAt(timeAgo);
		}
	}

//	public String saveImageToServer(MultipartFile file) throws IOException {
//	    if (file.isEmpty()) {
//	        throw new IllegalArgumentException("File is empty");
//	    }
//
//	    String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
//	    File directory = new File(UPLOAD_DIR);
//	    if (!directory.exists()) {
//	        boolean created = directory.mkdirs(); // Create the directory if it doesn't exist
//	        if (!created) {
//	            throw new IOException("Failed to create directory: " + UPLOAD_DIR);
//	        }
//	    }
//
//	    String originalFilename = file.getOriginalFilename();
//	    String extension = originalFilename.substring(originalFilename.lastIndexOf('.')); // File extension
//	    String newFilename = originalFilename.substring(0, originalFilename.lastIndexOf('.')) + "_" + timestamp
//	            + extension;
//
//	    // Handle image or video based on file extension
//	    if (isVideoFile(extension)) {
//	        // Save the video to a temporary location
//	        File tempVideoFilePath = new File(UPLOAD_DIR + newFilename);
//	        try (InputStream inputStream = file.getInputStream()) {
//	            Files.copy(inputStream, tempVideoFilePath.toPath(), StandardCopyOption.REPLACE_EXISTING);
//	        } catch (IOException e) {
//	            throw new IOException("Error while saving the video: " + e.getMessage(), e);
//	        }
//
//	        // Asynchronously compress the video
//	        compressVideoAsync(tempVideoFilePath);
//
//	        // Return the URL for the compressed video (after compression process finishes)
//	        return "https://dev.strishakti.org/uploads/strishakti/posts/posts/" + newFilename.replace(extension, "_compressed.mp4");
//	    } else if (isImageFile(extension)) {
//	        // Save the image (no compression needed)
//	        File imageFilePath = new File(UPLOAD_DIR + newFilename);
//	        try (InputStream inputStream = file.getInputStream()) {
//	            Files.copy(inputStream, imageFilePath.toPath(), StandardCopyOption.REPLACE_EXISTING);
//	        } catch (IOException e) {
//	            throw new IOException("Error while saving the image: " + e.getMessage(), e);
//	        }
//
//	        // Return the URL for the image
//	        return "https://dev.strishakti.org/uploads/strishakti/posts/posts/" + newFilename;
//	    } else {
//	        throw new IllegalArgumentException("Unsupported file type: " + extension);
//	    }
//	}
//
//	// Function to check if the file is a video
//	private boolean isVideoFile(String extension) {
//		return extension.equalsIgnoreCase(".mp4") || extension.equalsIgnoreCase(".mkv")
//				|| extension.equalsIgnoreCase(".avi");
//	}
//
//	// Function to check if the file is an image
//	private boolean isImageFile(String extension) {
//		return extension.equalsIgnoreCase(".jpg") || extension.equalsIgnoreCase(".jpeg")
//				|| extension.equalsIgnoreCase(".png");
//	}
//
//	// Asynchronous function to compress the video in a separate thread
//	private void compressVideoAsync(File videoFile) {
//	    ExecutorService executor = Executors.newSingleThreadExecutor();
//	    executor.submit(() -> {
//	        try {
//	            String compressedVideoPath = compressVideo(videoFile);
//	            // After compression, delete the original video file if needed
//	            if (videoFile.exists()) {
//	                if (!videoFile.delete()) {
//	                    System.out.println("Failed to delete the original video.");
//	                }
//	            }
//	        } catch (IOException e) {
//	            e.printStackTrace(); // Log the error, or handle it as needed
//	        }
//	    });
//	    executor.shutdown(); // Shutdown the executor after submission
//	}
//
//	// Function to compress the video
//	private String compressVideo(File videoFile) throws IOException {
//	    // Full path to ffmpeg executable
//	    String ffmpegPath = "F:/Sajid/software/ffmpeg-7.1-essentials_build/ffmpeg-7.1-essentials_build/bin/ffmpeg.exe";
//
//	    // Prepare the path for the compressed video
//	    String compressedFilename = videoFile.getName().substring(0, videoFile.getName().lastIndexOf('.'))
//	            + "_compressed.mp4";
//	    File compressedFilePath = new File(videoFile.getParent(), compressedFilename);
//
//	    // FFmpeg command to compress the video with higher compression settings
//	    String ffmpegCompressionCommand = ffmpegPath + " -i " + videoFile.getAbsolutePath()
//	            + " -vcodec libx264 -crf 35 -preset fast -acodec aac -strict -2 "
//	            + compressedFilePath.getAbsolutePath();
//
//	    // Use AtomicReference to make process effectively final
//	    AtomicReference<Process> processRef = new AtomicReference<>(null);
//
//	    try {
//	        // Execute the FFmpeg command to compress the video
//	        Process process = Runtime.getRuntime().exec(ffmpegCompressionCommand);
//	        processRef.set(process); // Store the process in AtomicReference
//
//	        // Capture the output and error streams (do not log them)
//	        Thread outputThread = new Thread(() -> {
//	            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//	                while (reader.readLine() != null) {
//	                    // Read output but do not log it
//	                }
//	            } catch (IOException e) {
//	                // Handle any exceptions quietly
//	            }
//	        });
//
//	        // Capture error stream (do not log it)
//	        Thread errorThread = new Thread(() -> {
//	            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
//	                while (reader.readLine() != null) {
//	                    // Read errors but do not log them
//	                }
//	            } catch (IOException e) {
//	                // Handle any exceptions quietly
//	            }
//	        });
//
//	        // Start the threads
//	        outputThread.start();
//	        errorThread.start();
//
//	        // Wait for the process to complete
//	        int exitCode = process.waitFor();
//
//	        // Wait for threads to finish
//	        outputThread.join();
//	        errorThread.join();
//
//	        if (exitCode != 0) {
//	            throw new IOException("FFmpeg failed to compress video. Exit code: " + exitCode);
//	        }
//
//	        // Return the compressed video path to use for URL
//	        return compressedFilePath.getAbsolutePath();
//
//	    } catch (IOException | InterruptedException e) {
//	        throw new IOException("Error while compressing video: " + e.getMessage(), e);
//	    } finally {
//	        Process process = processRef.get(); // Get the process from AtomicReference
//	        if (process != null) {
//	            process.destroy(); // Make sure to destroy the process after completion
//	        }
//	    }
//	}

	public String saveImageToServer(MultipartFile file) throws IOException {
		System.out.println("Image store");
		if (file.isEmpty()) {
			throw new IllegalArgumentException("File is empty");
		}

		String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		File directory = new File(UPLOAD_DIR);
		if (!directory.exists()) {
			boolean created = directory.mkdirs(); // Create the directory if it doesn't exist
			if (!created) {
				throw new IOException("Failed to create directory: " + UPLOAD_DIR);
			}
		}

		String originalFilename = file.getOriginalFilename();
		String extension = originalFilename.substring(originalFilename.lastIndexOf('.')); // File extension
		String newFilename = originalFilename.substring(0, originalFilename.lastIndexOf('.')) + "_" + timestamp
				+ extension;

//		// Handle image or video based on file extension
//		if (isVideoFile(extension)) {
//			// Save the video to a temporary location
//			File tempVideoFilePath = new File(VIDEO_DIR + newFilename);
//			try (InputStream inputStream = file.getInputStream()) {
//				Files.copy(inputStream, tempVideoFilePath.toPath(), StandardCopyOption.REPLACE_EXISTING);
//			} catch (IOException e) {
//				throw new IOException("Error while saving the video: " + e.getMessage(), e);
//			}
//			// Asynchronously compress the video
//			compressVideoAsync(tempVideoFilePath);
//
//			// Return the URL for the compressed video (after compression process finishes)
//			return UPLOAD_PATH_URL + COMP_VIDEO_BASE_PATH + newFilename.replace(extension, "_compressed.mp4");
//
//		} else if (isImageFile(extension)) {
//			System.out.println("Image");
		// Save the image (no compression needed)
		File imagedirectory = new File(UPLOAD_DIR);
		if (!imagedirectory.exists()) {
			boolean created = imagedirectory.mkdirs(); // Create the directory if it doesn't exist
			if (!created) {
				throw new IOException("Failed to create directory: " + UPLOAD_DIR);
			}
		}
		File imageFilePath = new File(UPLOAD_DIR + newFilename);
		try (InputStream inputStream = file.getInputStream()) {
			Files.copy(inputStream, imageFilePath.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new IOException("Error while saving the image: " + e.getMessage(), e);
		}

		// Return the URL for the image
		if ("dev".equals(activeProfile)) {
			return UPLOAD_PATH_URL + POST_BASE_PATH + newFilename;
		} else if ("prod".equals(activeProfile)) {
			return "https://dev.strishakti.org" + POST_BASE_PATH + newFilename;
		} else if ("local".equals(activeProfile)) {
			return "http://localhost:8085" + POST_BASE_PATH + newFilename;
		}
		return null; // Or handle other profiles
//		} 
//		else {
//			throw new IllegalArgumentException("Unsupported file type: " + extension);
//		}
	}

//	private void compressVideoAsync(File tempVideoFilePath) {
//		ExecutorService executor = Executors.newSingleThreadExecutor();
//		executor.submit(() -> {
//			try {
//				compressVideo(tempVideoFilePath);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			// After compression, delete the original video file if needed
//			if (tempVideoFilePath.exists()) {
//				if (!tempVideoFilePath.delete()) {
//					System.out.println("Failed to delete the original video.");
//				}
//			}
//		});
//		executor.shutdown(); // Shutdown the executor after submission
//
//	}

//	private void compressVideo(File videoFile) throws IOException {
//		// Log FFmpeg Path for debugging
//		System.out.println("FFMPEG_PATH: " + ffmpegPath);
//
//		// Prepare the path for the compressed video
//		String compressedFilename = videoFile.getName().substring(0, videoFile.getName().lastIndexOf('.'))
//				+ "_compressed.mp4";
//		File compressedFilePath = new File(videoFile.getParent(), compressedFilename);
//
//		// FFmpeg command to compress the video with higher compression settings
//		String ffmpegCompressionCommand = String.format(
//				"\"%s\" -i \"%s\" -vcodec libx264 -crf 35 -preset fast -acodec aac -strict -2 \"%s\"", ffmpegPath,
//				videoFile.getAbsolutePath(), compressedFilePath.getAbsolutePath());
//
//		// Use ProcessBuilder for better control
//		ProcessBuilder processBuilder = new ProcessBuilder(ffmpegPath, // FFmpeg executable path
//				"-i", videoFile.getAbsolutePath(), "-vcodec", "libx264", "-crf", "35", "-preset", "fast", "-acodec",
//				"aac", "-strict", "-2", compressedFilePath.getAbsolutePath());
//		processBuilder.redirectErrorStream(true); // Combine error and output for simplicity
//
//		try {
//			Process process = processBuilder.start();
//
//			// Capture and log FFmpeg output and error streams
//			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//					BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
//
//				String line;
//				while ((line = reader.readLine()) != null) {
//					System.out.println("FFmpeg Output: " + line); // Log output
//				}
//
//				String errorLine;
//				while ((errorLine = errorReader.readLine()) != null) {
//					System.out.println("FFmpeg Error: " + errorLine); // Log errors
//				}
//			}
//
//			// Wait for the process to finish
//			int exitCode = process.waitFor();
//			if (exitCode != 0) {
//				throw new IOException("FFmpeg failed to compress video. Exit code: " + exitCode);
//			}
//
//			System.out.println("Video compression completed successfully: " + compressedFilePath.getAbsolutePath());
////			return compressedFilePath.getAbsolutePath();
//		} catch (IOException | InterruptedException e) {
//			throw new IOException("Error while compressing video using FFmpeg command: " + ffmpegCompressionCommand, e);
//		}
//
//	}
//
//	// Function to check if the file is a video
//	private boolean isVideoFile(String extension) {
//		return extension.equalsIgnoreCase(".mp4") || extension.equalsIgnoreCase(".mkv")
//				|| extension.equalsIgnoreCase(".avi");
//	}
//
//	// Function to check if the file is an image
//	private boolean isImageFile(String extension) {
//		return extension.equalsIgnoreCase(".jpg") || extension.equalsIgnoreCase(".jpeg")
//				|| extension.equalsIgnoreCase(".png");
//	}

	public boolean deleteObject(String postImageUrl) {

		if (postImageUrl != null && !postImageUrl.isEmpty()) {
			String imageName = postImageUrl.substring(postImageUrl.lastIndexOf('/') + 1); // Extract the file name
			Path imagePath = Paths.get(UPLOAD_DIR + imageName);
			try {
				return Files.deleteIfExists(imagePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	public Long getUserIdFromRequest(HttpServletRequest request) {
		long idd = (Long) request.getAttribute("userId");
		return idd;
	}

	public void updateImage(Consumer<String> setImagePath, MultipartFile multipartFile) {
		if (multipartFile != null && !multipartFile.isEmpty()) {
			try {
				String imageUrl = saveImageToServer(multipartFile);
				setImagePath.accept(imageUrl);
			} catch (Exception e) {
				System.out.println("Error saving image: {}" + e.getMessage());
			}
		}
	}

//	public String generateVideoThumbnail(MultipartFile videoFile) {
//		System.out.println("Running as user: " + System.getProperty("user.name"));
//
//		try {
//			// Ensure absolute paths for both directories
//			Path thumbnailDir = Paths.get(THUMBNAIL_DIR).toAbsolutePath();
//			Path videoDir = Paths.get(VIDEO_DIR).toAbsolutePath();
//
//			// Create directory for saving thumbnail if it doesn't exist
//			if (!Files.exists(thumbnailDir)) {
//				Files.createDirectories(thumbnailDir);
//				LOGGER.info("Created thumbnail directory: " + thumbnailDir);
//			}
//
//			// Ensure unique filename for the video file by appending a UUID to the original
//			String originalFilename = videoFile.getOriginalFilename();
//			String uniqueVideoFilename = UUID.randomUUID().toString() + "_" + originalFilename;
//
//			// Remove any trailing dot from the filename
//			uniqueVideoFilename = uniqueVideoFilename.replaceAll("\\.$", "");
//
//			String videoFilePath = videoDir.resolve(uniqueVideoFilename).toAbsolutePath().toString();
//
//			// Save the video to the unique location
//			try (InputStream inputStream = videoFile.getInputStream()) {
//				Files.copy(inputStream, Paths.get(videoFilePath));
//				LOGGER.info("Video saved at: " + videoFilePath);
//			} catch (IOException e) {
//				LOGGER.log(Level.SEVERE, "Error saving video file", e);
//				throw new IOException("Error saving video file: " + e.getMessage());
//			}
//
//			// Log the video path to confirm the file location
//			LOGGER.info("Video file path: " + videoFilePath);
//
//			// Check if the file exists before proceeding
//			Path videoPath = Paths.get(videoFilePath);
//			if (!Files.exists(videoPath)) {
//				LOGGER.severe("Video file not found at: " + videoFilePath);
//				throw new IOException("Video file not found at: " + videoFilePath);
//			}
//
//			// Generate the thumbnail path using a timestamp-based filename
//			String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
//			String extension = ".jpg"; // Thumbnail will always be .jpg
//			String newFilename = UUID.randomUUID().toString() + "_" + timestamp + extension; // Unique filename with
//																								// UUID and timestamp
//			String thumbnailPath = thumbnailDir.resolve(newFilename).toString();
//
//			// Generate thumbnail using FFmpeg
//			try {
//				// Construct the FFmpeg command
//				String[] command = { ffmpegPath, // FFmpeg executable path
//						"-i", videoFilePath, // Input video file
//						"-ss", "00:00:01", // Seek to 1 second
//						"-vframes", "1", // Extract a single frame
//						"-update", "1", // Overwrite output file if it exists
//						thumbnailPath // Output thumbnail file path
//				};
//
//				// Log the FFmpeg command before executing it
//				LOGGER.info("Running FFmpeg command: " + String.join(" ", command));
//
//				// Execute the FFmpeg command
//				ProcessBuilder processBuilder = new ProcessBuilder(command);
//				processBuilder.redirectErrorStream(true); // Combine error and output streams
//				Process process = processBuilder.start();
//
//				// Handle the output and error streams
//				new Thread(() -> logStream(process.getInputStream(), "INFO")).start();
//				new Thread(() -> logStream(process.getErrorStream(), "ERROR")).start();
//
//				// Wait for the process to finish
//				int exitCode = process.waitFor();
//				if (exitCode == 0) {
//					LOGGER.info("Thumbnail generated at: " + thumbnailPath);
//				} else {
//					throw new IOException("FFmpeg command failed with exit code " + exitCode);
//				}
//
//			} catch (IOException | InterruptedException e) {
//				LOGGER.log(Level.SEVERE, "Error generating thumbnail", e);
//				throw new IOException("Error generating thumbnail: " + e.getMessage());
//			}
//
//			// Optionally, delete the uploaded video file if it's no longer needed
//			try {
//				Files.delete(Paths.get(videoFilePath));
//				LOGGER.info("Deleted the video file: " + videoFilePath);
//			} catch (IOException e) {
//				LOGGER.log(Level.WARNING, "Error deleting video file", e);
//			}
//
//			// Return the generated thumbnail URL with the correct filename format
//			return UPLOAD_PATH_URL + THUMBNAIL_BASE_PATH + newFilename;
//
//		} catch (IOException e) {
//			LOGGER.log(Level.SEVERE, "Error generating video thumbnail", e);
//			// Propagate the error as a runtime exception to be handled at a higher level
//			throw new RuntimeException("Error processing video file for thumbnail generation: " + e.getMessage(), e);
//		}
//	}

	public String generateVideoThumbnail(MultipartFile videoFile) {
		System.out.println("Running as user: " + System.getProperty("user.name"));

		try {
			// Ensure absolute paths for both directories
			Path thumbnailDir = Paths.get(THUMBNAIL_DIR).toAbsolutePath();
			Path videoDir = Paths.get(VIDEO_DIR).toAbsolutePath();

			// Create directories if they don't exist
			if (!Files.exists(thumbnailDir)) {
				Files.createDirectories(thumbnailDir);
				LOGGER.info("Created thumbnail directory: " + thumbnailDir);
			}
			if (!Files.exists(videoDir)) {
				Files.createDirectories(videoDir);
				LOGGER.info("Created video directory: " + videoDir);
			}

			// Validate original filename
			String originalFilename = videoFile.getOriginalFilename();
			if (originalFilename == null || originalFilename.trim().isEmpty()) {
				throw new IllegalArgumentException("Invalid video file name.");
			}

			// Sanitize filename and ensure uniqueness
			originalFilename = originalFilename.replaceAll("\\s+", "_");
			String uniqueVideoFilename = UUID.randomUUID().toString() + "_" + originalFilename;
			uniqueVideoFilename = uniqueVideoFilename.replaceAll("\\.$", ""); // remove trailing dot

			String videoFilePath = videoDir.resolve(uniqueVideoFilename).toAbsolutePath().toString();

			// Save the uploaded video file
			try (InputStream inputStream = videoFile.getInputStream()) {
				Files.copy(inputStream, Paths.get(videoFilePath));
				LOGGER.info("Video saved at: " + videoFilePath);
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Error saving video file", e);
				throw new IOException("Error saving video file: " + e.getMessage());
			}

			// Confirm file was saved
			Path videoPath = Paths.get(videoFilePath);
			if (!Files.exists(videoPath)) {
				LOGGER.severe("Video file not found at: " + videoFilePath);
				throw new IOException("Video file not found at: " + videoFilePath);
			}

			// Prepare thumbnail filename
			String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			String extension = ".jpg";
			String newFilename = UUID.randomUUID().toString() + "_" + timestamp + extension;
			String thumbnailPath = thumbnailDir.resolve(newFilename).toString();

			// Generate thumbnail with FFmpeg
			try {
				String[] command = { ffmpegPath, "-i", videoFilePath, "-ss", "00:00:01", "-vframes", "1", "-update",
						"1", thumbnailPath };

				LOGGER.info("Running FFmpeg command: " + String.join(" ", command));

				ProcessBuilder processBuilder = new ProcessBuilder(command);
				processBuilder.redirectErrorStream(true);
				Process process = processBuilder.start();

				new Thread(() -> logStream(process.getInputStream(), "INFO")).start();
				new Thread(() -> logStream(process.getErrorStream(), "ERROR")).start();

				int exitCode = process.waitFor();
				if (exitCode == 0) {
					LOGGER.info("Thumbnail generated at: " + thumbnailPath);
				} else {
					throw new IOException("FFmpeg command failed with exit code " + exitCode);
				}

			} catch (IOException | InterruptedException e) {
				LOGGER.log(Level.SEVERE, "Error generating thumbnail", e);
				throw new IOException("Error generating thumbnail: " + e.getMessage());
			}

			// Optional: Delete the video file
			try {
				Files.delete(videoPath);
				LOGGER.info("Deleted the video file: " + videoFilePath);
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, "Error deleting video file", e);
			}

			// Return the final URL
			return UPLOAD_PATH_URL + THUMBNAIL_BASE_PATH + newFilename;

		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error generating video thumbnail", e);
			throw new RuntimeException("Error processing video file for thumbnail generation: " + e.getMessage(), e);
		}
	}

	private void logStream(InputStream inputStream, String logLevel) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if ("INFO".equals(logLevel)) {
					LOGGER.info(line);
				} else {
					LOGGER.warning(line);
				}
			}
		} catch (IOException e) {
			LOGGER.severe("Error reading FFmpeg output: " + e.getMessage());
			e.printStackTrace();
		}
	}

//	public String createAndStoreVideoThumbnail(MultipartFile videoFile) throws IOException {
//		if (videoFile.isEmpty()) {
//			throw new IllegalArgumentException("Video file is empty");
//		}
//
//		// Create timestamp for the filename
//		String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
//
//		// Directory to save thumbnails
//		File directory = new File(UPLOAD_DIR + "thumbnails/");
//		if (!directory.exists()) {
//			boolean created = directory.mkdirs(); // Create the directory if it doesn't exist
//			if (!created) {
//				throw new IOException("Failed to create directory: " + UPLOAD_DIR + "thumbnails/");
//			}
//		}
//
//		// Get the original video file name and extension
//		String originalFilename = videoFile.getOriginalFilename();
//		String extension = originalFilename.substring(originalFilename.lastIndexOf('.')); // Video file extension
//
//		// Use a timestamp-based filename to avoid issues with long filenames or special
//		// characters
//		String newFilename = timestamp + extension;
//
//		// Save the video file temporarily
//		File videoFilePath = new File(UPLOAD_DIR + newFilename);
//		try (InputStream inputStream = videoFile.getInputStream()) {
//			Files.copy(inputStream, videoFilePath.toPath(), StandardCopyOption.REPLACE_EXISTING);
//		} catch (IOException e) {
//			throw new IOException("Error while saving the video: " + e.getMessage(), e);
//		}
//
//		// Create the thumbnail file name
//		String thumbnailFilename = newFilename.substring(0, newFilename.lastIndexOf('.')) + "_thumbnail.jpg";
//		File thumbnailFilePath = new File(directory, thumbnailFilename);
//
//		// Full path to ffmpeg executable (use the absolute path)
//		String ffmpegPath = "F:\\Sajid\\software\\ffmpeg-7.1-essentials_build\\ffmpeg-7.1-essentials_build\\bin\\ffmpeg.exe"; // Full
//																																// path
//																																// to
//																																// ffmpeg.exe
//		String ffmpegCommand = ffmpegPath + " -i " + videoFilePath.getAbsolutePath() + " -ss 00:00:03 -vframes 1 "
//				+ thumbnailFilePath.getAbsolutePath();
//
//		try {
//			// Execute the FFmpeg command to generate the thumbnail
//			Process process = Runtime.getRuntime().exec(ffmpegCommand);
//			process.waitFor();
//
//			// Check for errors in the process execution
//			if (process.exitValue() != 0) {
//				throw new IOException("FFmpeg failed to generate thumbnail. Exit code: " + process.exitValue());
//			}
//		} catch (IOException | InterruptedException e) {
//			throw new IOException("Error while generating thumbnail: " + e.getMessage(), e);
//		}
//
//		// Return the public URL of the thumbnail
//		String thumbnailUrl = "https://dev.strishakti.org/uploads/strishakti/posts/thumbnails/" + thumbnailFilename;
//		return thumbnailUrl;
//	}
	/*
	 * public void deleteCacheByPatternUsingScan(String pattern) throws IOException
	 * { Set<String> keysToDelete = new HashSet<>();
	 * 
	 * // Access the Redis connection from RedisTemplate RedisConnection connection
	 * = redisTemplate.getConnectionFactory().getConnection();
	 * 
	 * // Start scanning with the provided pattern ScanOptions scanOptions =
	 * ScanOptions.scanOptions().match(pattern).count(1000).build(); Cursor<byte[]>
	 * cursor = connection.scan(scanOptions);
	 * 
	 * try { // Loop through the cursor to retrieve all matching keys while (cursor
	 * != null && cursor.getCursorId() != 0) { while (cursor.hasNext()) { String key
	 * = new String(cursor.next(), StandardCharsets.UTF_8); // Ensure correct
	 * decoding log.info("Matching key: {}", key); // Log the key
	 * keysToDelete.add(key); // Add to the set of keys to delete }
	 * 
	 * // Continue scanning until cursor ID is 0 if (cursor.getCursorId() != 0) {
	 * cursor = connection.scan(scanOptions); // Continue the scan with the same
	 * options } }
	 * 
	 * // If there are keys to delete, proceed with deletion if
	 * (!keysToDelete.isEmpty()) { redisTemplate.delete(keysToDelete); // Delete
	 * keys in bulk log.info("Deleted cache keys: {}", keysToDelete); } else {
	 * log.info("No matching keys found for pattern: {}", pattern); } } finally {
	 * cursor.close(); // Ensure the cursor is closed even in case of exceptions } }
	 */

//	@Async
//	public void deleteCacheForAllUsers() {
//		// Get all users and delete their timeline cache keys
//		List<User> allUsers = userRepo.findAll();
//		List<String> keysToDelete = new ArrayList<>();
//
//		for (User user : allUsers) {
//			String targetRedisKey = "targetUserTimeline:" + user.getUuid();
//			keysToDelete.add(targetRedisKey);
//		}
//
//		redisTemplate.delete(keysToDelete);
//		log.info("Cache deleted for all impacted timelines: " + keysToDelete);
//	}

	public void mentionNotification(User sender, User user, Posts postEntity) {
		// Create notification for the mentioned user
		System.out.println("In mentioned notification>>>>>>>>>>>>>>>");
		Notification notification = new Notification();
		notification.setSender(sender); // The user who created the post
		notification.setReceiver(user); // The user being mentioned
		notification
				.setNotificationMessage(sender.getFullName() + " mentioned you in a post: " + postEntity.getPostName());
		notification.setNotificationType(NotificationType.MENTION); // Notification type for mentions
		notification.setNotificationStatus(NotificationStatus.UNREAD); // The default status when a notification is
																		// created
		// Save the notification to the database
		notificationRepo.save(notification);

		System.out.println("notification for mention savedddddd>>>>>>>>>>>>>>");
	}

	@Scheduled(cron = "5 34 13 * * *")
	@Transactional
	public void BirthDayNotifications() {
		List<User> userList = useRepo.findByuserDateOfBirth();
		List<Notification> birtdaynotify = notificationRepo.findByTypeBirthDay();

		if (!birtdaynotify.isEmpty() && birtdaynotify != null) {
			try {
				for (Notification notification : birtdaynotify) {
					LocalDateTime currentDateTime = LocalDateTime.now();
					LocalDateTime thresholdDateTime = currentDateTime.minus(3, ChronoUnit.DAYS);
					if (notification.getNotificationCreatedAt().isBefore(thresholdDateTime)) {
						notificationRepo.deleteNotification(notification.getNotificationId());
						System.out.println("Notification deleted>>>>" + notification.getNotificationId());
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("No other data found" + e);
			}
		}

		if (!userList.isEmpty() && userList != null) {
			for (User user : userList) {
				List<User> friends = friendRequestRepo.findFriendsBySenderReceiverApproved(user.getUserId());
				if (!friends.isEmpty() && friends != null) {
					for (User friend : friends) {
						String birthdayDate = LocalDate.now().toString();
						Notification notify = new Notification();
						notify.setNotificationMessage(
								user.getFullName() + CommonMessages.Birthday_notification + birthdayDate);
						notify.setNotificationStatus(NotificationStatus.UNREAD);
						notify.setNotificationType(NotificationType.BIRTHDAY);
						notify.setReceiver(friend);
						notificationRepo.save(notify);
					}
				}
			}
		}
		List<Notification> allNotifications = notificationRepo.findAll();
		if (allNotifications != null && !allNotifications.isEmpty()) {
			for (Notification notifyy : allNotifications) {
				LocalDateTime currentDateTime = LocalDateTime.now();
				LocalDateTime timeTirtyDaysBefore = currentDateTime.minus(30, ChronoUnit.DAYS);
				if (notifyy.getNotificationCreatedAt().isBefore(timeTirtyDaysBefore)) {

					notificationRepo.deleteNotification(notifyy.getNotificationId());
					System.out.println("Notification deleted>>>>" + notifyy.getNotificationId());
				}
			}
		}

	}

}