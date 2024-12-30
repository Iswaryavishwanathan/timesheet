// package com.raison.timesheet.controller;

// import java.util.HashMap;
// import java.util.Map;

// // import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// // import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import com.raison.timesheet.DTO.LoginRequest;
// import com.raison.timesheet.service.UserService;

// // import jakarta.servlet.http.HttpServletRequest;

// @RestController
// @RequestMapping("/api/user")
// public class UserController {

//      @Autowired
//     private UserService userService;


//       // Step 1: Send random number to frontend
//       @PostMapping("/sendRandomNumber")
//       public ResponseEntity<Map<String, Object>> sendRandomNumber(@RequestParam String username, @RequestParam String ipAddress) {
//           int randomNumber = userService.generateAndStoreRandomNumber(username, ipAddress);
//           Map<String, Object> response = new HashMap<>();
//           response.put("randomNumber", randomNumber); // Send random number to frontend
//           return ResponseEntity.ok(response);
//       }

//       @PostMapping("/login")
//     public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest, @RequestParam String ipAddress) {
       
//         Map<String, Object> response = new HashMap<>();
       

//         // If the login is successful
//         boolean isValid = userService.verifyLogin(loginRequest, ipAddress);

        
//         if  (isValid){
//             response.put("isValid", true);
//             return ResponseEntity.ok(response);
//         } else {
//             response.put("isValid", false);
//             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//         }
//     }
// }


package com.raison.timesheet.controller;

// import java.util.HashMap;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.raison.timesheet.DTO.LoginRequest;
import com.raison.timesheet.service.UserService;



@Controller
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // Step 1: Serve the login page with a form for the user to fill out
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login"; // This corresponds to the Thymeleaf template (login.html)
    }

    // Step 2: Handle the form submission and generate random number for IP address
    @PostMapping("/login")
    public String handleLogin(@ModelAttribute("loginRequest") LoginRequest loginRequest, Model model, @RequestParam String ipAddress) {
        // Fetch the random number associated with the username and IP address
        int randomNumber = userService.generateAndStoreRandomNumber(loginRequest.getUsername(), ipAddress);

        // Encode password using the random number and check login credentials
        String encodedPassword = Integer.toString(loginRequest.getPassword().hashCode() + randomNumber);

        // Create a new LoginRequest object for validation
        LoginRequest encodedLoginRequest = new LoginRequest(loginRequest.getUsername(), encodedPassword);

        // Verify login using the encoded password and username
        boolean isValid = userService.verifyLogin(encodedLoginRequest, ipAddress);

        if (isValid) {
            model.addAttribute("message", "Login successful!");
            return "welcome"; // Redirect to a success page (welcome.html)
        } else {
            model.addAttribute("message", "Login failed! Invalid username or password.");
            return "login"; // Stay on the login page with error message
        }
    }

    // Step 3: Provide a random number via an API for testing purposes (if needed)
    @PostMapping("/sendRandomNumber")
    public ResponseEntity<Map<String, Object>> sendRandomNumber(@RequestParam String username, @RequestParam String ipAddress) {
        int randomNumber = userService.generateAndStoreRandomNumber(username, ipAddress);
        Map<String, Object> response = new HashMap<>();
        response.put("randomNumber", randomNumber); // Send random number to frontend
        return ResponseEntity.ok(response);
    }
   
}


