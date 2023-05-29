package entities;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ticket")
@Getter
@Setter
@NoArgsConstructor
public class Ticket extends Travel_Document {

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "vehicle_id")
	private Vehicle vehicle;

	private LocalDate dataVid;
	private boolean endorsed;

	public Ticket(LocalDate dataEmissione, AuthorizedDealer puntoEmissione, boolean endorsed, User u) {
		super(dataEmissione, puntoEmissione);
		this.endorsed = endorsed;
		this.user = u;
	}

	@Override
	public String toString() {
		return "Titolo n°: " + id + " data di emissione: " + dataEmissione + " , Punto di emissione: " + puntoEmissione
				+ " Timbrato: " + endorsed + " , Acquistato dall' " + user;
	};

}
