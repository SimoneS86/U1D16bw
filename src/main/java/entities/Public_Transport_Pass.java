package entities;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pass")
@Getter
@Setter
@NoArgsConstructor
public class Public_Transport_Pass extends Travel_Document {

	public enum SubType {
		SETTIMANALE, MENSILE
	};

	@Enumerated(EnumType.STRING)
	protected SubType subType;

	private boolean isValid;

	@OneToOne
	@JoinColumn(name = "card_Id")
	private Card card;

	public Public_Transport_Pass(LocalDate dataEmissione, AuthorizedDealer puntoEmissione, SubType subType,
			boolean isValid, Card card) {
		super(dataEmissione, puntoEmissione);
		this.subType = subType;
		this.isValid = isValid;
		this.card = card;
	}

	public Card getCard() {
		return card;
	}

	@Override
	public String toString() {
		return "Titolo n°: " + id + " data di emissione: " + dataEmissione + " , Punto di emissione: " + puntoEmissione
				+ " Tipologia abbonamento: " + subType + " , Validità: " + isValid + " , " + card;
	};

}