package com.sayan.auth.myauthappbeckend.controller;

import com.sayan.auth.myauthappbeckend.dtos.UserDTO;
import com.sayan.auth.myauthappbeckend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> createUser(
            @RequestBody UserDTO userDTO
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDTO));
    }

    @GetMapping
    public ResponseEntity<Iterable<UserDTO>> getAllUsers(){
        return ResponseEntity.status(HttpStatus.FOUND).body(userService.getAllUsers());
    }

    @GetMapping(params = "email")
    public ResponseEntity<UserDTO> getUserByEmail(@RequestParam String email){
        return ResponseEntity.status(HttpStatus.FOUND).body(userService.getUserByEmail(email));
    }

    @GetMapping(params = "id")
    public ResponseEntity<UserDTO> getUserByID(@RequestParam String id){
        return ResponseEntity.status(HttpStatus.FOUND).body(userService.getUserByID(id));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") String userId){
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body("Deletion Successful");
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO, @PathVariable("userId") String userId){
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(userDTO,userId));
    }

}
