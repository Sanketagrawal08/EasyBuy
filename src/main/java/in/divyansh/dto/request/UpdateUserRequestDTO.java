package in.divyansh.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequestDTO {

    @Size(min = 2, max = 100,
          message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @Email(message = "Please provide a valid email address")
    @Size(max = 150,
          message = "Email must not exceed 150 characters")
    private String email;

    @Size(min = 6, max = 100,
          message = "Password must be between 6 and 100 characters")
    private String password;

    @Pattern(regexp = "^[0-9]{10}$",
             message = "Phone number must contain exactly 10 digits")
    private String phone;

    @Size(min = 2, max = 255,
          message = "Address must be between 2 and 255 characters")
    private String address;
}