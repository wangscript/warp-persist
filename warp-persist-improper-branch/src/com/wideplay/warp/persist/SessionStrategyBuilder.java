package com.wideplay.warp.persist;

/**
 * Created with IntelliJ IDEA.
 * On: 2/06/2007
 *
 * @author Dhanji R. Prasanna
 * @since 1.0
 */
public interface SessionStrategyBuilder extends PersistenceModuleBuilder{
    TransactionStrategyBuilder across(UnitOfWork unitOfWork);
}
