package com.sayan.auth.myauthappbeckend.services;

import com.sayan.auth.myauthappbeckend.dtos.UserDTO;
import com.sayan.auth.myauthappbeckend.entities.Provider;
import com.sayan.auth.myauthappbeckend.entities.User;
import com.sayan.auth.myauthappbeckend.exceptions.UserNotFoundException;
import com.sayan.auth.myauthappbeckend.repositories.UserRepository;
import com.sayan.auth.myauthappbeckend.utils.UserHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        if(userDTO.getEmail()==null && userDTO.getEmail().isBlank()){
            throw new IllegalArgumentException("Email is required");
        }

        if(userRepository.existsByEmail(userDTO.getEmail())){
            throw new IllegalArgumentException("Email already exists");
        }
        User user = modelMapper.map(userDTO, User.class);
        user.setProvider(userDTO.getProvider()!=null ? userDTO.getProvider() : Provider.LOCAL );
        userRepository.save(user);
        //Role Assign
        //TODO:
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserDTO getUserByID(String userId) {
        User user = userRepository.findById(UserHelper.parseUUID(userId)).orElseThrow(UserNotFoundException::new);
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    @Transactional
    public UserDTO updateUser(UserDTO userDTO, String userId) {
        User user =  userRepository.findById(UserHelper.parseUUID(userId)).orElseThrow(UserNotFoundException::new);
        user.setName(userDTO.getName()!=null? userDTO.getName() : user.getName());
        user.setImage(userDTO.getImage()!=null? userDTO.getImage() : user.getImage());
        user.setPassword(userDTO.getPassword()!=null? userDTO.getPassword() : user.getPassword());
        user.setProvider(userDTO.getProvider()!=null? userDTO.getProvider() : user.getProvider());
        user.setEnabled(userDTO.isEnabled());
        user.setUpdatedAt(Instant.now());
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    public void deleteUser(String userID) {
        userRepository.deleteById(UserHelper.parseUUID(userID));
    }

    @Override
    @Transactional
    public Iterable<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(user -> modelMapper.map(user, UserDTO.class)).toList();
    }
}
