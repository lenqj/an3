package SINU.sinu.service;

import SINU.sinu.dto.MyUserAuthDTO;
import SINU.sinu.entities.EmailPayload;
import SINU.sinu.entities.MyUser;

public interface EmailService {
    void sendEmail (EmailPayload emailPayload);
    void sendRegistrationEmail(MyUserAuthDTO myUserAuthDTO);
}
