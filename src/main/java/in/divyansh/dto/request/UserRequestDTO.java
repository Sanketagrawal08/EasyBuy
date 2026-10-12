package in.divyansh.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class UserRequestDTO {

	@NotBlank(message="Fullname  is Required")
	@Size(min=2,max=100,message = "fullname must  be between 2 and 100 chars")
    private String fullName ;
	
	@NotBlank(message="Email  is Required")
	@Email(message = "Please provide a valid email  address")
	@Size(max=150,message = "Email must no Exceed 150 characters")
    private String email  ;
	
	@NotBlank(message = "Password is required")
	@Size(max=150,message = "password must be btw 6 and 100 chars")
    private String password ;
	
	@Pattern(regexp = "^[0-9]{10}$",message = "phone no must be of exact 10 digits")
    private String phone ;
	
	@Size(min=2,max=255,message = "Address must  be between 2 and 255 chars")
    private String address ;



}
