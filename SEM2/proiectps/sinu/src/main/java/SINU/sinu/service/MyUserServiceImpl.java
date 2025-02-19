package SINU.sinu.service;

import SINU.sinu.dto.MyUserDTO;
import SINU.sinu.dto.RegistrationRequestDTO;
import SINU.sinu.entities.MyUser;
import SINU.sinu.mapper.MyUserMapper;
import SINU.sinu.repository.MyUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyUserServiceImpl implements MyUserService{

    private final MyUserRepository userRepository;

    private final MyUserMapper userMapper;


    @Override
    public boolean checkEmail(String email) {
        return userRepository.existsByEmailAddress(email);
    }
    @Override
    public MyUserDTO registerUser(RegistrationRequestDTO registrationRequest) {
        MyUser user = MyUser.builder()
                .username(registrationRequest.getUsername())
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .password(registrationRequest.getPassword())
                .emailAddress(registrationRequest.getEmailAddress())
                .build();


        return this.createUser(user);
    }

    public MyUserDTO getLoginUser(){
        return userMapper.userEntityToDto(Objects.requireNonNull(userRepository.findLoginUser().orElse(null)));
    }

    public MyUserDTO getUserById(Integer id){
        return userMapper.userEntityToDto(Objects.requireNonNull(userRepository.findById(id).orElse(null)));
    }
    public List<MyUserDTO> getAllUsers(){
        return userMapper.userListEntityToDto(userRepository.findAll());
    }

    @Override
    public List<MyUser> findAll() {
        return userRepository.findAll();
    }

    public MyUserDTO createUser(MyUser user){
        user.setPassword(user.getPassword());
        return userMapper.userEntityToDto(userRepository.save(user));
    }

    public MyUser updateUser(MyUserDTO user, String password){
        return userRepository.save(userMapper.userDtoToEntity(user, password));
    }

    public void deleteUser(MyUser user){
        userRepository.delete(user);
    }

    @Override
    public void deleteById(Integer ID) {
        userRepository.deleteById(ID);
    }

    @Override
    public Optional<MyUser> findById(Integer ID) {
        return userRepository.findById(ID);
    }
    public void save(MyUser user) {
        userRepository.save(user);
    }
}
