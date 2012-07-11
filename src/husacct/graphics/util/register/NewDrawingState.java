package husacct.graphics.util.register;

import husacct.common.dto.AbstractDTO;
import husacct.common.dto.AnalysedModuleDTO;
import husacct.common.dto.DependencyDTO;
import husacct.common.dto.ModuleDTO;
import husacct.common.dto.ViolationDTO;
import husacct.graphics.presentation.figures.BaseFigure;
import husacct.graphics.presentation.figures.RelationFigure;

import java.util.ArrayList;
import java.util.HashMap;

public class NewDrawingState {
	private NewDrawingState parentState;
	private String fullPath;
	private HashMap<String, NewFigureMap> paths;
	private HashMap<RelationFigure, DependencyDTO[]> dependencyDTOMap;
	private HashMap<RelationFigure, ViolationDTO[]> violationDTOMap;
	private HashMap<BaseFigure, Double> figurePositions;
	
	private int maxDependencies, maxViolations, maxAll;
	
	public NewDrawingState(String path){
		fullPath = path;
		paths = new HashMap<String, NewFigureMap>();
		dependencyDTOMap = new HashMap<RelationFigure, DependencyDTO[]>(); 
		violationDTOMap = new HashMap<RelationFigure, ViolationDTO[]>();
	}
	
	public void setParentState(NewDrawingState state){
		parentState = state;
	}
	
	public NewDrawingState getParentState(){
		return parentState;
	}
	
	public String getFullPath(){
		return fullPath;
	}
	
	public ArrayList<String> getPaths() {
		return new ArrayList<String>(paths.keySet());
	}
	
	private void addPath(String path) {
		if (!paths.containsKey(path)) {
			paths.put(path, new NewFigureMap());
		}
	}
	
	private NewFigureMap getPath(String path){
		return paths.get(path);
	}

	public void addFigure(String subPath, BaseFigure figure, AbstractDTO dto) {
		addPath(subPath);
		NewFigureMap map = getPath(subPath);
		map.addFigure(figure, dto);
	}
	
	public AbstractDTO getFigureDTO(BaseFigure figure){
		AbstractDTO dto = null;
		for(NewFigureMap map : paths.values()){
			if(map.containsFigure(figure)){
				dto = map.getFigureDTO(figure);
			}
		}
		return dto;
	}
	
	public String getParentFigurePath(BaseFigure figure){
		String path = null;
		for(String key : paths.keySet()){
			if(paths.get(key).containsFigure(figure)){
				path = key;
			}
		}
		return path;
	}

	public void addContextFigure(BaseFigure contextFigure) {
		if (null != parentState) {
			AbstractDTO contextDTO = parentState.getFigureDTO(contextFigure);
			String path = null;
			if(contextDTO instanceof ModuleDTO){
				path = ((ModuleDTO) contextDTO).logicalPath;
			}else if (contextDTO instanceof AnalysedModuleDTO){
				path = ((AnalysedModuleDTO) contextDTO).uniqueName;
			}else{
				throw new RuntimeException("DTO type not supported");
			}
			addFigure(path, contextFigure, contextDTO);
		}
	}

	public ArrayList<BaseFigure> getFiguresByPath(String path) {
		NewFigureMap pathMap = paths.get(path);
		return pathMap.getFigures();
	}

	public void addDependency(RelationFigure relationFigure, DependencyDTO[] dtos) {
		setMaxDependencies(dtos.length);
		dependencyDTOMap.put(relationFigure, dtos);
	}

	public DependencyDTO[] getDependencyDTOs(RelationFigure relationFigure) {
		return dependencyDTOMap.get(relationFigure);
	}

	public void addViolation(RelationFigure relationFigure, ViolationDTO[] dtos) {
		setMaxViolations(dtos.length);
		violationDTOMap.put(relationFigure, dtos);
	}
	
	public ViolationDTO[] getViolationDTOs(RelationFigure relationFigure) {
		return violationDTOMap.get(relationFigure);
	}
	
	private void setMaxDependencies(int newMax){
		if(newMax > maxDependencies){
			maxDependencies = newMax;
		}
		if(newMax > maxAll){
			maxAll = newMax;
		}
	}
	
	public int getMaxDependencies(){
		return maxDependencies;
	}
	
	private void setMaxViolations(int newMax){
		if(newMax > maxViolations){
			maxViolations = newMax;
		}
		if(newMax > maxAll){
			maxAll = newMax;
		}
	}
	
	public int getMaxViolations(){
		return maxViolations;
	}
	
	public int getMaxAll(){
		return maxAll;
	}
}
