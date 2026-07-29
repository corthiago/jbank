package com.thiago.jbank.repository;

import com.thiago.jbank.entities.Wallet;
import com.thiago.jbank.repository.dto.StatementView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    String SQL_STATEMENT = """
            SELECT
              BIN_TO_UUID(transfer_id) as statement_id,
              "transfer" as type,
              amount as statement_value,
              BIN_TO_UUID(wallet_receiver_id) as wallet_receiver,
              BIN_TO_UUID(wallet_sender_id) as wallet_sender,
              date_time as statement_date_time
            FROM
              transfers
            WHERE
              wallet_receiver_id = UUID_TO_BIN(?1) OR wallet_sender_id = UUID_TO_BIN(?1)
            UNION ALL
            SELECT
              BIN_TO_UUID(deposit_id) as statement_id,
              "deposit" as type,
              amount as statement_value,
              BIN_TO_UUID(wallet_id) as wallet_receiver,
              "" as wallet_sender,
              date_time as statement_date_time
            FROM
              deposits
            WHERE
              wallet_id = UUID_TO_BIN(?1)
            """;

    String SQL_COUNT_STATEMENT = """
            SELECT COUNT(*)
            FROM (
                """ + SQL_STATEMENT + """
            ) as total
            """;

    Optional<Wallet> findByDocumentNumberOrEmail(String documentNumber, String email);

    @Query(value = SQL_STATEMENT, countQuery = SQL_COUNT_STATEMENT, nativeQuery = true)
    Page<StatementView> findStatements(String walletId, PageRequest pageRequest);

}
