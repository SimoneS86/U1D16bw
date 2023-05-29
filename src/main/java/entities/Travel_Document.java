package entities;

import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@NoArgsConstructor
public abstract class Travel_Document {
	
	@Id
	@GeneratedValue
	protected UUID id;
	
	protected LocalDate dataEmissione;
	
	@ManyToOne
	@JoinColumn(name = "vehicle_id")
	protected Vehicle vehicle;
	
	@ManyToOne
	@JoinColumn(name = "authorizeddealer_id")
	protected AuthorizedDealer puntoEmissione;

	
	public Travel_Document(LocalDate dataEmissione, AuthorizedDealer puntoEmissione) {
		this.dataEmissione = dataEmissione;
		this.puntoEmissione = puntoEmissione;
		 if (puntoEmissione != null) {
	            this.puntoEmissione.incrementDocCounter();
	            this.puntoEmissione.getIssuedDocuments().add(this); 
	        }
	}

}
