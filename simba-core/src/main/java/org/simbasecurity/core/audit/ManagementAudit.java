package org.simbasecurity.core.audit;

import org.simbasecurity.core.domain.Session;
import org.simbasecurity.core.domain.repository.SessionRepository;
import org.simbasecurity.core.service.thrift.ThriftTokenAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.text.MessageFormat.format;

@Service
public class ManagementAudit {

    private static final int MAX_MESSAGE_LENGTH = 512;
    private final Audit audit;
    private final AuditLogEventFactory factory;
    private final SessionRepository sessionRepository;

    @Autowired
    public ManagementAudit(Audit audit,
                           AuditLogEventFactory factory,
                           SessionRepository sessionRepository) {
        this.audit = audit;
        this.factory = factory;
        this.sessionRepository = sessionRepository;
    }

    public void log(String pattern, Object... arguments) {
        String ssoToken = ThriftTokenAccess.get();
        if (ssoToken != null) {
            Session session = sessionRepository.findBySSOToken(ssoToken);
            String format = format(pattern, arguments);
            if (format.length() > MAX_MESSAGE_LENGTH) format = format.substring(0, MAX_MESSAGE_LENGTH);
            audit.log(factory.createEventForManagement(session.getUser().getUserName(), session.getSSOToken(), format));
        }
    }

}
