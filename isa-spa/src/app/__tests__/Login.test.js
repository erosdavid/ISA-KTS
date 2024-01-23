import {render, screen} from "@testing-library/react";
import {LoginPage} from "../pages/login/LoginPage";
import {IsaButton} from "../components/isa-button/IsaButton";
import axios from "axios";
import { MemoryRouter as Router } from "react-router-dom";

describe('test login component', function () {
    test("render login component", () => {
        render(<LoginPage />);
        const buttonlist = screen.findAllByRole("button");

        expect(buttonlist).toHaveLength(3)




    })
});

