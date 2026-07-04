package de.simone.vaadinplus.views.components;

import de.simone.MainLayout;
import de.simone.frontend.HomeView;
import de.simone.vaadinplus.components.Step;
import de.simone.vaadinplus.components.Stepper;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PermitAll
@PageTitle("Steppers")
@Route(value = "steppers", layout = MainLayout.class)
public class SteppersView extends ComponentView {

    public SteppersView() {
        addH2("Horizontal");
        add(createHorizontalStepper());

        addH2("Horizontal & small");
        Stepper stepper = createHorizontalStepper();
        stepper.setSmall(true);
        add(stepper);

        addH2("Vertical");
        add(createVerticalStepper());

        addH2("Vertical & small");
        stepper = createVerticalStepper();
        stepper.setSmall(true);
        add(stepper);
    }

    private Stepper createHorizontalStepper() {
        Step step1 = new Step("Shopping cart", HomeView.class);
        Step step2 = new Step("Shipping & billing", SteppersView.class);
        Step step3 = new Step("Payment method", HomeView.class);
        Step step4 = new Step("Confirmation", HomeView.class);

        Stepper stepper = new Stepper(step1, step2, step3, step4);
        stepper.setOrientation(Stepper.Orientation.HORIZONTAL);
        stepper.setState(Step.State.COMPLETE, step1);

        return stepper;
    }

    private Stepper createVerticalStepper() {
        Step step1 = new Step("Quantum flux analysis", "Initiate the quantum flux analysis for dimensional harmonization", AppBarsView.class);
        Step step2 = new Step("Nebulicious configuration", "Configure the nebulicious parameters for intergalactic harmonization", BreadcrumbsView.class);
        Step step3 = new Step("Hyperspectral calibrations111", "Perform hyperspectral calibrations to optimize quantum entanglement matrices", SteppersView.class);
        Step step4 = new Step("Transmogrification validation", "Validate transmogrification integrity for inter-realm transference.", CheckboxesView.class);

        Stepper stepper = new Stepper(step1, step2, step3, step4);
        stepper.setState(Step.State.COMPLETE, step2);
        stepper.setState(Step.State.ERROR, step2);

        return stepper;
    }

}
