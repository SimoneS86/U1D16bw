package dao;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import entities.AuthorizedDealer;
import entities.Card;
import entities.Public_Transport_Pass;
import entities.Travel_Document;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Travel_DocumentDAO {

	private final EntityManager em;

	public Travel_DocumentDAO(EntityManager em) {
		this.em = em;
	}

	public void save(Travel_Document a) {
		EntityTransaction t = em.getTransaction();
		try {
			t.begin();
			em.persist(a);
			t.commit();
			log.info("Travel Document saved!");
		} catch (Exception e) {
			if (t.isActive()) {
				t.rollback();
			}
			log.error("Errore durante il salvataggio del veicolo.", e);
			throw e;
		}

	}
	
	public void update(Travel_Document travelDoc) {
        EntityTransaction t = em.getTransaction();
        try {
            t.begin();
            em.merge(travelDoc);
            t.commit();
            log.info("Titolo di viaggio aggiornato!");
        } catch (Exception e) {
            if (t.isActive()) {
                t.rollback();
            }
            log.error("Errore durante l'aggiornamento del documento di viaggio.", e);
            throw e;
        }
    }

	public List<Travel_Document> getAllTd() {
		try {
			TypedQuery<Travel_Document> query = em.createQuery("SELECT c FROM Travel_Document c",
					Travel_Document.class);
			List<Travel_Document> tdList = query.getResultList();
			StringBuilder td = new StringBuilder("Lista Titoli di Viaggio:\n");
			for (Travel_Document travelDoc : tdList) {
				td.append(travelDoc.toString()).append("\n");
			}
			System.out.println(td.toString());
			return tdList;
		} catch (Exception e) {
			log.error("error: " + e);
			throw e;
		}

	};

	public Travel_Document findByUUID(String id) {
		Travel_Document found = em.find(Travel_Document.class, UUID.fromString(id));
		return found;
	}

	public void delete(String id) {
		try {
			Travel_Document found = em.find(Travel_Document.class, UUID.fromString(id));
			if (found != null) {
				em.remove(found);
				System.out.println("Travel Document with id " + id + " deleted!");
			}
		} catch (Exception e) {
			log.info("Delete error: " + e);
		}

	}

	public void checkValidity(String idPass, String idCard) {
		Public_Transport_Pass pass = em.find(Public_Transport_Pass.class, UUID.fromString(idPass));
		Card card = em.find(Card.class, UUID.fromString(idCard));

		if (pass.getSubType() == Public_Transport_Pass.SubType.SETTIMANALE && card.isValidità() == true) {

			LocalDate dataEmissione = pass.getDataEmissione();
			LocalDate now = LocalDate.now();
			LocalDate expirationDate = dataEmissione.plusWeeks(1);

			if (now.isBefore(expirationDate)) {
				System.out.println("L'abbonamento settimanale è ancora valido.");
			} else {
				pass.setValid(false);
				System.out.println("L'abbonamento settimanale non è più valido.");
			}
		} else if (pass.getSubType() == Public_Transport_Pass.SubType.MENSILE && card.isValidità() == true) {

			LocalDate dataEmissione = pass.getDataEmissione();
			LocalDate now = LocalDate.now();
			LocalDate expirationDate = dataEmissione.plusMonths(1);

			if (now.isBefore(expirationDate)) {
				System.out.println("L'abbonamento mensile è ancora valido.");
			} else {
				pass.setValid(false);
				System.out.println("L'abbonamento mensile non è più valido.");
			}
		} else {
			System.out.println("Card dell'utente scaduta.");
		}
	}

	public int docPerDealersAndDate(AuthorizedDealer authorizedDealer, LocalDate startDate, LocalDate endDate) {
		try {
			List<Travel_Document> documents = getDocumentsByAuthorizedDealer(authorizedDealer);
			int count = (int) documents.stream()
					.filter(document -> isWithinDateRange(document.getDataEmissione(), startDate, endDate)).count();
			log.info("Numero di documenti emessi per il punto di emissione: " + count + "\n");
			return count;
		} catch (Exception e) {
			log.error("Si è verificato un errore durante il conteggio dei documenti.", e);
			return 0;
		}
	}

	private List<Travel_Document> getDocumentsByAuthorizedDealer(AuthorizedDealer authorizedDealer) {
		try {
			List<Travel_Document> found = authorizedDealer.getIssuedDocuments();
			return found;
		} catch (Exception e) {
			log.error("Si è verificato un errore durante il conteggio dei documenti.", e);
			return null;
		}

	}

	private boolean isWithinDateRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
		return !date.isBefore(startDate) && !date.isAfter(endDate);
	}
}
