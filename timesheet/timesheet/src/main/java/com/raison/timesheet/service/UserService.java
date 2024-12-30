// package com.raison.timesheet.service;

// // import java.util.ArrayList;
// // import java.util.List;
// import java.util.Optional;
// import java.util.Random;


// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import com.raison.timesheet.DTO.LoginRequest;
// import com.raison.timesheet.Entity.RandomNumberLog;
// import com.raison.timesheet.Entity.UserCredential;
// import com.raison.timesheet.Repository.RandomNumberLogRepository;
// import com.raison.timesheet.Repository.UserCredentialRepository;

// @Service
// public class UserService {

//     @Autowired
//     private UserCredentialRepository userCredentialRepository;

//     @Autowired
//     private RandomNumberLogRepository randomNumberLogRepository;
//      // Step 1: Generate and store random number
//      public int generateAndStoreRandomNumber(String username, String ipAddress) {
//         int randomNumber = new Random().nextInt(999999); // Generate random number

//         // Check if log already exists
//         Optional<RandomNumberLog> existingLog = randomNumberLogRepository.findByUsernameAndIpAddress(username, ipAddress);
//         if (existingLog.isPresent()) {
//             RandomNumberLog log = existingLog.get();
//             log.setRandomNumber(randomNumber); // Update the random number
//             randomNumberLogRepository.save(log);
//         } else {
//             // Create a new log entry if it doesn't exist
//             RandomNumberLog log = new RandomNumberLog(username, ipAddress, randomNumber);
//             randomNumberLogRepository.save(log);
//         }
//         return randomNumber; // Return the generated random number
//     }
//      // Step 2: Verify login
//      public boolean verifyLogin(LoginRequest loginRequest, String ipAddress) {
//         String username = loginRequest.getUsername();
//         String encodedValue = loginRequest.getPassword(); // Encoded value from frontend (password + random number)

//         // Retrieve random number for the username and IP address
//         Optional<RandomNumberLog> logOptional = randomNumberLogRepository.findByUsernameAndIpAddress(username, ipAddress);
//         if (logOptional.isEmpty()) {
//             throw new RuntimeException("Random number not found for username: " + username + " and IP address: " + ipAddress);
//         }

//         int randomNumber = logOptional.get().getRandomNumber();

//         // Retrieve user credentials from the database
//         UserCredential user = userCredentialRepository.findByUsername(username);
//         if (user == null) {
//             throw new RuntimeException("User not found");
//         }

//         // Decode the encoded value (subtract random number to get the original password hash)
//         int decodedHash = Integer.parseInt(encodedValue) - randomNumber;

//         // Verify the decoded hash matches the stored password hash
//         return user.getPassword().hashCode() == decodedHash;
//     }
// }
//     // List to store usernames
//     private List<String> usernameList = new ArrayList<>();

//     public int generateRandomNumber(String username, String ipAddress) {
//         int randomNumber = new Random().nextInt(999999);
//         // // Add username to the list if not already present
//         if (!usernameList.contains(username)) {
//             usernameList.add(username);
//         }
//         Optional<RandomNumberLog> existingLog = randomNumberLogRepository.findByUsernameAndIpAddress(username, ipAddress);
    
//     if (existingLog.isPresent()) {
//         // Update the random number in the existing log
//         RandomNumberLog log = existingLog.get();
//         log.setRandomNumber(randomNumber);
//         randomNumberLogRepository.save(log);
//     } else {
//         // Create a new log entry if it doesn't exist
//         RandomNumberLog log = new RandomNumberLog(username, ipAddress, randomNumber);
//         randomNumberLogRepository.save(log);
//     }
    
//     return randomNumber;
//     }

//    // Decode the value sent by the frontend and validate credentials
//    public String decodeAndLogin(LoginRequest loginRequest) {


//     Optional<RandomNumberLog> log = randomNumberLogRepository.findByUsername(loginRequest.getUsername());
//     if (log.isEmpty()) {
//         return "Random number not found";
//     }

//     int randomNumber = log.get().getRandomNumber();
//     int decodedPassword = Integer.parseInt(loginRequest.getEncodedValue()) - randomNumber;

//     UserCredential user = userCredentialRepository.findByUsername(loginRequest.getUsername());
//     if (user == null || user.getPassword().hashCode() != decodedPassword) {
//         return "Invalid username or password";
//     }

//     // Cleanup after successful login
//     randomNumberLogRepository.delete(log.get());
//     return "Login successful";
// }




package com.raison.timesheet.service;

import com.raison.timesheet.DTO.LoginRequest;
import com.raison.timesheet.Entity.RandomNumberLog;
import com.raison.timesheet.Entity.UserCredential;
import com.raison.timesheet.Repository.RandomNumberLogRepository;
import com.raison.timesheet.Repository.UserCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    @Autowired
    private RandomNumberLogRepository randomNumberLogRepository;

    // Step 1: Generate and store random number for the username and IP address
    public int generateAndStoreRandomNumber(String username, String ipAddress) {
        int randomNumber = new Random().nextInt(999999); // Generate a random number

        // Check if log already exists for the given username and IP address
        Optional<RandomNumberLog> existingLog = randomNumberLogRepository.findByUsernameAndIpAddress(username, ipAddress);
        if (existingLog.isPresent()) {
            RandomNumberLog log = existingLog.get();
            log.setRandomNumber(randomNumber); // Update the random number
            randomNumberLogRepository.save(log);
        } else {
            RandomNumberLog log = new RandomNumberLog(username, ipAddress, randomNumber);
            randomNumberLogRepository.save(log);
        }

        return randomNumber; // Return the generated random number
    }

    // Step 2: Verify login by decoding the password
    public boolean verifyLogin(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String encodedPassword = loginRequest.getPassword(); // The encoded password sent from the frontend

        // Retrieve random number for the username from the database
        Optional<RandomNumberLog> logOptional = randomNumberLogRepository.findByUsername(username);
        if (logOptional.isEmpty()) {
            return false; // Random number not found for the user
        }

        int randomNumber = logOptional.get().getRandomNumber();

        // Retrieve user credentials from the database
        UserCredential user = userCredentialRepository.findByUsername(username);
        if (user == null) {
            return false; // User not found
        }

        // Decode the password by subtracting the random number
        int decodedHash = Integer.parseInt(encodedPassword) - randomNumber;

        // Verify if the decoded password hash matches the stored password hash
        return user.getPassword().hashCode() == decodedHash;
    }
}
