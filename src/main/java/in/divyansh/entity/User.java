package in.divyansh.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table(name="users") // jpa bnayega table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
	 @Id
	 @GeneratedValue(strategy=GenerationType.IDENTITY)
     private Long id;
	 
	 @Column(nullable=false,length=100)
     private String fullName ;
	 
	 @Column(nullable=false,unique = true,length = 150)
     private String email  ;
	 
	 @Column(nullable=false)
     private String password ;
	 
	 @Column(length=15)
     private String phone ;
	 
	 @Column(length=255)
     private String address ;
	 
	 @Column(nullable=false)
     @Builder.Default // builder pattern mei kisi chiz ko intilize ese krna ho we can use this annotation
     private  Boolean isActive=true ;
	 
    @OneToOne(mappedBy="user",cascade=CascadeType.ALL,orphanRemoval=true)
     private Cart cart;
	 
	 @OneToMany(mappedBy="user",cascade=CascadeType.ALL,orphanRemoval=true)
     @Builder.Default
     private List<Order> orders=new ArrayList<>();
     
}
