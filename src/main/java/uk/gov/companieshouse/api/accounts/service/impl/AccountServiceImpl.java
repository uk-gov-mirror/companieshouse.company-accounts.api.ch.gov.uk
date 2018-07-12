package uk.gov.companieshouse.api.accounts.service.impl;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.LinkType;
import uk.gov.companieshouse.api.accounts.model.entity.AccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Account;
import uk.gov.companieshouse.api.accounts.repository.AccountRepository;
import uk.gov.companieshouse.api.accounts.service.AccountService;
import uk.gov.companieshouse.api.accounts.transformer.AccountTransformer;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Resource
    private AccountTransformer accountTransformer;

    /**
     * {@inheritDoc}
     */
    public Account createAccount(Account account) {
        generateEtagLinksKind(account);

        AccountEntity accountEntity = accountTransformer.transform(account);

        accountRepository.insert(accountEntity);

        return account;
    }

    private void generateEtagLinksKind(Account account) {
        account.setEtag(GenerateEtagUtil.generateEtag());
        account.setKind(Kind.ACCOUNT.getValue());

        Map<String, String> links = new HashMap<>();

        links.put(LinkType.SELF.getLink(), "");
        account.setLinks(links);
    }
}