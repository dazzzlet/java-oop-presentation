package com.netcompany.repository;

import java.util.List;

import com.netcompany.entity.BalanceSheet;

public interface GpaBalanceSheetRepository {

    void add(BalanceSheet balanceSheet);

    void update(BalanceSheet balanceSheet);

    void delete(BalanceSheet balanceSheet);

    BalanceSheet findByCreateOn(long createOn);

    List<BalanceSheet> getAll();
}
