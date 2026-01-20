package com.sayan.auth.myauthappbeckend.services;

import com.sayan.auth.myauthappbeckend.dtos.UserDTO;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO getUserByEmail(String email);
    UserDTO getUserByID(String userId);
    UserDTO updateUser(UserDTO userDTO,String userId);
    void deleteUser(String userID);
    Iterable<UserDTO> getAllUsers();
}
