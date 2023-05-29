package entities;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NamedQuery(name = "VendingMachine.findAll", query = "SELECT a FROM VendingMachine a")
@Getter
@Setter
@NoArgsConstructor
public class VendingMachine extends AuthorizedDealer{
	

	private boolean outOfOrder;

	public VendingMachine(boolean outOfOrder) {
		super();
		this.outOfOrder = outOfOrder;
	}
	
	@Override
	public String toString() {
		return "VendingMachine [outOfOrder=" + outOfOrder + "]";
	}
	public void ticketBought() {
	    try {
	        if (isOutOfOrder()) {
	            System.out.println("The vending machine is out of order.");
	        } else {
	            System.out.println("Ticket is being printed, please wait.");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
}