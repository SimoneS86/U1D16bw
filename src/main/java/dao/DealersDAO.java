package dao;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import entities.AuthorizedDealer;
import entities.VendingMachine;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DealersDAO {

	private final EntityManager em;

	public DealersDAO(EntityManager em) {
		this.em = em;
	}

	public List<AuthorizedDealer> getAllAuthorizedDealers() {
		try {
			TypedQuery<AuthorizedDealer> query = em.createNamedQuery("AuthorizedDealer.findAll",
					AuthorizedDealer.class);
			List<AuthorizedDealer> authorizedDealersList = query.getResultList();
			StringBuilder dealers = new StringBuilder("Lista Rivenditori Autorizzati:\n");
			for (AuthorizedDealer dealer : authorizedDealersList) {
				dealers.append(dealer.toString()).append("\n");
			}
			System.out.println(dealers.toString());
			return authorizedDealersList;
		} catch (Exception e) {
			log.error("Error: " + e);
			throw e;
		}
	}

	public void saveAuthorizedDealer(AuthorizedDealer authorizedDealer) {
		EntityTransaction et = em.getTransaction();
		try {
			et.begin();
			em.persist(authorizedDealer);
			et.commit();
		} catch (Exception e) {
			et.rollback();
			log.error("Error: " + e);
		}
	}

	public AuthorizedDealer getAuthorizedDealerById(String id) {
		return em.find(AuthorizedDealer.class, UUID.fromString(id));
	}

	public void updateAuthorizedDealer(AuthorizedDealer authorizedDealer) {
		EntityTransaction et = em.getTransaction();
		try {
			et.begin();
			em.merge(authorizedDealer);
			et.commit();
		} catch (Exception e) {
			et.rollback();
			log.error("Error: " + e);
		}
	}

	public void deleteAuthorizedDealer(AuthorizedDealer authorizedDealer) {
		EntityTransaction et = em.getTransaction();
		try {
			et.begin();
			em.remove(em.contains(authorizedDealer) ? authorizedDealer : em.merge(authorizedDealer));
			et.commit();
		} catch (Exception e) {
			et.rollback();
			log.error("Error: " + e);
		}
	}

	public List<VendingMachine> getAllVendingMachines() {
		try {
			TypedQuery<VendingMachine> query = em.createNamedQuery("VendingMachine.findAll", VendingMachine.class);
			List<VendingMachine> vendingMachines = query.getResultList();
			for (VendingMachine machine : vendingMachines) {
				log.info(machine.toString());
			}
			return vendingMachines;
		} catch (Exception e) {
			log.error("Error: " + e);
			throw e;
		}
	}

	public void close() {
		em.close();
	}
}