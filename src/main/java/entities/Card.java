package entities;

import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import entities.Public_Transport_Pass.SubType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "card")
@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class Card {
	@Id
	@GeneratedValue
	protected UUID Id;
	protected LocalDate dataAttivazione;
	protected LocalDate dataScadenza;
	protected boolean validità;
	protected double credit;
	
	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

	@OneToOne(mappedBy = "card")
	@JoinColumn(name = "pass_id")
	private Public_Transport_Pass pass;

	public Card(LocalDate dataAttivazione) {
		this.dataAttivazione = dataAttivazione;
		this.dataScadenza = dataAttivazione.plusYears(1);
		this.validità = false;
		this.credit = 0.00;
	};
	
	public void toUpCredit(double credito) {
			
			if(validità == false) {
				
				this.credit =+ credito;
				
				if(credit>= 200){
					
					this.dataAttivazione = LocalDate.now();
					this.dataScadenza = dataAttivazione.plusYears(1);
					this.credit = credit - 200;
					this.validità = true;
					log.info("You have purchased the annual pass for 200$" + " / you have a credit of: " + credit);
					
				}else {
					
					log.warn("you don't have enough credit.");
					
				}
				
			}else{
				log.info("The card is still valid");
			};	
	};
	
	@Override
	public String toString() {
		return "Tessera n°= " + Id + " data di attivazione: " + dataAttivazione + " data di scadenza: " + dataScadenza
				+ " validità: " + validità;
	};
}
