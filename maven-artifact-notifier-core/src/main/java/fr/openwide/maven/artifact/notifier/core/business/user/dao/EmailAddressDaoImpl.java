package fr.openwide.maven.artifact.notifier.core.business.user.dao;

import org.springframework.stereotype.Repository;

import fr.openwide.core.jpa.business.generic.dao.GenericEntityDaoImpl;
import fr.openwide.maven.artifact.notifier.core.business.user.model.EmailAddress;

@Repository("emailAddressDao")
public class EmailAddressDaoImpl extends GenericEntityDaoImpl<Long, EmailAddress> implements IEmailAddressDao {

}
