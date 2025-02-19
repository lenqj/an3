package SINU.sinu.mapper;

import SINU.sinu.dto.MyUserDTO;
import SINU.sinu.entities.MyUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class MyUserMapper {

    public MyUserDTO userEntityToDto(MyUser user){
        return MyUserDTO.builder()
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .emailAddress(user.getEmailAddress())
                .build();
    }

    public List<MyUserDTO> userListEntityToDto(List<MyUser> users){
        return users.stream()
                .map(this::userEntityToDto)
                .toList();
    }

    public MyUser userDtoToEntity(MyUserDTO userDto, String password){
        return MyUser.builder()
                .username(userDto.getUsername())
                .password(password)
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .emailAddress(userDto.getEmailAddress())
                .build();
    }

    public List<MyUser> userListDtoToEntity(List<MyUserDTO> userDtos, String password){
        return userDtos.stream()
                .map(userDto -> userDtoToEntity(userDto, password))
                .toList();
    }
}
