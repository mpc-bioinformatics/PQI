package de.mpc.pqi.view;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.mpc.pqi.model.PeptideModel;
import de.mpc.pqi.model.ProteinModel;
import de.mpc.pqi.view.diagram.BoxPlotChart;
import de.mpc.pqi.view.diagram.PQICategoryChart;
import de.mpc.pqi.view.table.Table;
import de.mpc.pqi.view.transform.AbundanceValueType;
import de.mpc.pqi.view.tree.ProteinTree;
import de.mpc.pqi.view.tree.ProteinTree.ProteinTreeSelectionListener;
import de.mpc.pqi.view.tree.ProteinTreeModel;

public class ProteinView extends JPanel {
	private static final long serialVersionUID = -4267390339147137516L;
	private ProteinTree tree;
	private PQICategoryChart chart;
	private Table table;
	private BoxPlotChart boxPlotChart;

	private AbundanceValueType abundanceValueType = AbundanceValueType.ABUNDANCE;

	public ProteinView() {
		initGUI();
		initLayout();
		initControl();
	}

	private void initGUI() {
		tree = new ProteinTree();
		chart = new PQICategoryChart();
		table = new Table();
		boxPlotChart = new BoxPlotChart();
	}

	private void initLayout() {
		// setLayout(new BorderLayout());
		//
		// add(tree, BorderLayout.WEST);
		// add(chart.createChart(null), BorderLayout.CENTER);
		// add(table.initTable(null), BorderLayout.SOUTH);

		setLayout(new GridBagLayout());
		GridBagHelper constraints = new GridBagHelper(new double[] { 0.1, 0.1 }, new double[] { 0.1, 0.5, 0.2 });
		add(tree, constraints.getConstraints(0, 0));
		add(chart.createChart(null, abundanceValueType), constraints.getConstraints(1, 0));
		add(table.initTable(null, abundanceValueType), constraints.getConstraints(0, 1, 3, 1));
		add(boxPlotChart.getView(null), constraints.getConstraints(2, 0));
	}

	private void initControl() {
		tree.addSelectionListener(new ProteinTreeSelectionListener() {
			@Override
			public void selectionChanged(Object selection) {
				if (selection instanceof PeptideModel) {
					PeptideModel peptideModel = (PeptideModel) selection;
					chart.updateChartData(peptideModel);
				} else if (selection instanceof ProteinModel) {
					ProteinModel proteinModel = (ProteinModel) selection;
					chart.updateChartData(proteinModel);
					table.setData(proteinModel, abundanceValueType);
				}
			}
		});

		table.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				if (!e.getValueIsAdjusting()) {
					List<PeptideModel> peptideModels = new ArrayList<>();

					for (int i : table.getSelectedRows()) {
						if (i == 0) {
							chart.updateChartData(table.getProteinModel());
						} else {
							peptideModels.add(table.getValueAt(i - 1));
						}
					}
					if (!peptideModels.isEmpty()) {
						chart.updateChartData(peptideModels);
						boxPlotChart.update(peptideModels.get(0));
					}

				}
			}
		});

		tree.addAbundanceValueButtonListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equalsIgnoreCase("abundance")) {
					abundanceValueType = AbundanceValueType.ABUNDANCE;
				} else if (e.getActionCommand().equalsIgnoreCase("log10")) {
					abundanceValueType = AbundanceValueType.LOG10;
				} else if (e.getActionCommand().equalsIgnoreCase("arcsin")) {
					abundanceValueType = AbundanceValueType.ARCSIN;
				}

				table.updateValueType(abundanceValueType);
				chart.updateValueType(abundanceValueType);
			}
		});
	}

	public void setModel(List<ProteinModel> list) {
		tree.setModel(new ProteinTreeModel(list));
	}
}
