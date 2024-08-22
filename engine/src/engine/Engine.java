package engine;

import cell.Cell;
import cell.dto.CellBasicDetails;
import cell.dto.CellDetails;
import cell.dto.UpdateCellDto;
import engine.semaphore.ISemaphoreTask;
import engine.semaphore.SemaphoreTask;
import filter.dto.MultiColumnsFilterConfig;
import position.interfaces.IPosition;
import range.CellRange;
import filter.dto.FilterConfig;
import sheet.dto.SortConfig;
import sheet.interfaces.ISheet;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

public final class Engine implements IEngine {
    private final ISheet sheet;
    private final Semaphore semaphore = new Semaphore(1);

    public Engine(ISheet sheet) {
        this.sheet = sheet;
    }

    @Override
    public void updateCell(UpdateCellDto updateCellDto) {
        safeExecute(() -> {
            sheet.updateCell(updateCellDto);
            return null;
        });
    }

    @Override
    public Map<IPosition, Cell> getHistory(int version) {
        return safeExecute(() -> sheet.getPastVersion(version));
    }

    @Override
    public Map<Integer, Integer> getUpdateCountList() { return safeExecute(sheet::getUpdate2VersionCount); }

    @Override
    public String getSheetName() {
        return safeExecute(sheet::getName);
    }

    @Override
    public CellBasicDetails getCellBasicDetails(IPosition position) {
        return safeExecute(() -> sheet.getCellBasicDetails(position));
    }

    @Override
    public CellDetails getCellDetails(IPosition position) {
        return safeExecute(() -> sheet.getCellDetails(position));
    }

    @Override
    public void addRange(CellRange range) {
        safeVoidExecute(() -> sheet.addRange(range));
    }

    @Override
    public void removeRange(CellRange range) {
        safeVoidExecute(() -> sheet.removeRange(range));
    }

    @Override
    public List<CellRange> getRanges() {
        return safeExecute(sheet::getRanges);
    }

    @Override
    public List<Cell> viewCellsInRange(CellRange range) {
        return safeExecute(() -> sheet.viewCellsInRange(range));
    }

    @Override
    public List<Integer> getRowsByFilter(FilterConfig filterConfig) {
        return safeExecute(() -> sheet.getRowsByFilter(filterConfig.range(), filterConfig.selectedValues()));
    }

    @Override
    public List<Integer> sortRowsInRange(SortConfig sortConfig) {
        return safeExecute(() -> sheet.sortRowsInRange(sortConfig.range(), sortConfig.columns(), sortConfig.ascending()));
    }

    @Override
    public Map<IPosition, Cell> getWhatIfCells(List<UpdateCellDto> updateCellDtos) {
        return safeExecute(() -> sheet.getWhatIfCells(updateCellDtos));
    }

    @Override
    public List<Integer> getRowsByMultiColumnsFilter(MultiColumnsFilterConfig filterConfig) {
        return safeExecute(() -> sheet.getRowsByMultiColumnsFilter(filterConfig.range(), filterConfig.selectedValues(), filterConfig.isAnd()));
    }

    private <T> T safeExecute(ISemaphoreTask<T> task) {
        try {
            semaphore.acquire();
            return task.execute();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted", e);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            semaphore.release();
        }
    }

    private void safeVoidExecute(SemaphoreTask task) {
        safeExecute(() -> {
            task.execute();
            return null;
        });
    }
}

