package in.divyansh.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne(fetch=FetchType.LAZY)
	// since cart relationship ki owner hai so we use joincoloumn 
	@JoinColumn(name="user_id",nullable=false,unique=true)
	private User user;
	
	@OneToMany(mappedBy="cart", cascade=CascadeType.ALL,orphanRemoval=true)
	@Builder.Default
	private List<CartItem>cartItems=new ArrayList<>();
	
	@Column
	@Builder.Default
	private double totalAmount=0.0;
	
	@Column(nullable=false)
	@Builder.Default	
	private Integer totalItems=0;
	
//	this is a annotation of hibernate 
	@CreationTimestamp
	@Column(updatable=false) //updatable=false so nobody can tamper the updatetimestamp
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	
	//Helper Methods 
	
	public void addCartItem(CartItem item) {
		cartItems.add(item); 
//		set the back refrence bec the relationship is bidirectional
		item.setCart(this);
		recalculateTotals();
	}
	
	
	public void removeCartItem(CartItem item) {
		cartItems.remove(item);
		item.setCart(null);
		recalculateTotals();
	}
	
	
	public void recalculateTotals() {
		int totalItemCount=0;
		for(CartItem item:cartItems) {
			totalItemCount+=item.getQuantity();
		}
		this.totalItems=totalItemCount;
		double total=0.0;
		for(CartItem item:cartItems) {
			total=total+item.getSubTotal();
		}
		this.totalAmount=total;
	}
	
	public void clearCart() {
//		we are doing dirtyOnWrite concept over here
		for(CartItem item:new ArrayList<>(cartItems)) {
			item.setCart(null);
		}
		cartItems.clear();
		this.totalAmount=0.0;
		this.totalItems=0;	
	}

}
