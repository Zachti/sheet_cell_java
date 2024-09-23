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
import range.IRange;
import sheet.dto.SortConfig;
import sheet.history.SheetHistory;
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
    public SheetHistory getHistory(int version) {
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
    public void removeRange(String rangeName) {
        safeVoidExecute(() -> sheet.removeRangeOrThrow(rangeName));
    }

    @Override
    public List<IRange> getRanges() {
        return safeExecute(sheet::getRanges);
    }

    @Override
    public Map<IPosition, Cell> viewCellsInRange(CellRange range) {
        return safeExecute(() -> sheet.viewCellsInRange(range));
    }

    @Override
    public Map<IPosition, Cell> getCellsByFilter(FilterConfig filterConfig) {
        return safeExecute(() -> sheet.getCellsByFilter(filterConfig.range(), filterConfig.selectedValues()));
    }

    @Override
    public List<Integer> sortRowsInRange(SortConfig sortConfig) {
        return safeExecute(() -> sheet.sortRowsInRange(sortConfig.range(), sortConfig.columns(), sortConfig.ascending()));
    }

    @Override
    public Map<IPosition, Cell> getWhatIfCells(UpdateCellDto updateCellDto) {
        return safeExecute(() -> sheet.getWhatIfCells(updateCellDto));
    }

    @Override
    public Map<IPosition, Cell> getRowsByMultiColumnsFilter(MultiColumnsFilterConfig filterConfig) {
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

