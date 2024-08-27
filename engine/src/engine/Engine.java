package engine;

import cell.Cell;
import cell.dto.CellBasicDetails;
import cell.dto.CellDetails;
import cell.dto.UpdateCellDto;
import common.thread.job.interfaces.IJob;
import common.thread.job.interfaces.IJobQueue;
import common.thread.job.JobQueue;
import filter.dto.MultiColumnsFilterConfig;
import position.interfaces.IPosition;
import range.CellRange;
import filter.dto.FilterConfig;
import sheet.dto.SortConfig;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static sheet.SheetsManager.getSheetById;

public final class Engine implements IEngine {
    private final static int MAX_CONCURRENT_JOBS = 10;
    private final static int MAX_QUEUE_SIZE = 20;
    private final IJobQueue jobQueue = new JobQueue(MAX_CONCURRENT_JOBS, MAX_QUEUE_SIZE);

    private Engine() {}

    @Override
    public Future<Void> updateCell(UpdateCellDto updateCellDto, UUID id) {
        return addVoidJobToQueue(() -> getSheetById(id).updateCell(updateCellDto));
    }

    @Override
    public Future<Map<IPosition, Cell>> getHistory(int version, UUID id) {
        return addJobToQueue(() -> getSheetById(id).getPastVersion(version));
    }

    @Override
    public Future<Map<Integer, Integer>> getUpdateCountList(UUID id) { return addJobToQueue(getSheetById(id)::getUpdate2VersionCount); }

    @Override
    public Future<String> getSheetName(UUID id) {
        return addJobToQueue(getSheetById(id)::getName);
    }

    @Override
    public Future<CellBasicDetails> getCellBasicDetails(IPosition position, UUID id) {
        return addJobToQueue(() -> getSheetById(id).getCellBasicDetails(position));
    }

    @Override
    public Future<CellDetails> getCellDetails(IPosition position, UUID id) {
        return addJobToQueue(() -> getSheetById(id).getCellDetails(position));
    }

    @Override
    public Future<Void> addRange(CellRange range, UUID id) {
        return addVoidJobToQueue(() -> getSheetById(id).addRange(range));
    }

    @Override
    public Future<Void> removeRange(CellRange range, UUID id) {
        return addVoidJobToQueue(() -> getSheetById(id).removeRange(range));
    }

    @Override
    public Future<List<CellRange>> getRanges(UUID id) {
        return addJobToQueue(getSheetById(id)::getRanges);
    }

    @Override
    public Future<List<Cell>> viewCellsInRange(CellRange range, UUID id) {
        return addJobToQueue(() -> getSheetById(id).viewCellsInRange(range));
    }

    @Override
    public Future<List<Integer>> getRowsByFilter(FilterConfig filterConfig, UUID id) {
        return addJobToQueue(() -> getSheetById(id).getRowsByFilter(filterConfig.range(), filterConfig.selectedValues()));
    }

    @Override
    public Future<List<Integer>> sortRowsInRange(SortConfig sortConfig, UUID id) {
        return addJobToQueue(() -> getSheetById(id).sortRowsInRange(sortConfig.range(), sortConfig.columns(), sortConfig.ascending()));
    }

    @Override
    public Future<Map<IPosition, Cell>> getWhatIfCells(List<UpdateCellDto> updateCellDtos, UUID id) {
        return addJobToQueue(() -> getSheetById(id).getWhatIfCells(updateCellDtos));
    }

    @Override
    public Future<List<Integer>> getRowsByMultiColumnsFilter(MultiColumnsFilterConfig filterConfig, UUID id) {
        return addJobToQueue(() -> getSheetById(id).getRowsByMultiColumnsFilter(filterConfig.range(), filterConfig.selectedValues(), filterConfig.isAnd()));
    }

    private <T> Future<T> addJobToQueue(Callable<T> job) {
        return jobQueue.addJob(job);
    }

    private Future<Void> addVoidJobToQueue(IJob job) {
        return addJobToQueue(() -> {
            job.execute();
            return null;
        });
    }

    private static class EngineHolder {  private static final Engine INSTANCE = new Engine(); }

    public static Engine getInstance() { return EngineHolder.INSTANCE; } // Bill Pugh Singleton design pattern
}

