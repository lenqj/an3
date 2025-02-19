package SINU.sinu.service;


import SINU.sinu.dto.MyUserDTO;
import SINU.sinu.dto.RegistrationRequestDTO;
import SINU.sinu.entities.MyUser;

import java.util.List;
import java.util.Optional;

public interface MyUserService {

    boolean checkEmail(String email);

    MyUserDTO registerUser(RegistrationRequestDTO myUserDTO);

    MyUserDTO getLoginUser();

    MyUserDTO getUserById(Integer id);

    List<MyUserDTO> getAllUsers();
    List<MyUser> findAll();


    MyUserDTO createUser(MyUser user);

    MyUser updateUser(MyUserDTO user, String password);

    void deleteUser(MyUser user);
    void deleteById(Integer ID);
    Optional<MyUser> findById(Integer ID);
    void save(MyUser user);
}
