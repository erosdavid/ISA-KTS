import {fireEvent, render, screen} from "@testing-library/react";
import {LoginPage} from "../pages/login/LoginPage";
import { BrowserRouter as Router } from 'react-router-dom';
import userEvent from "@testing-library/user-event";
import { act } from '@testing-library/react';


describe('test login component', () => {
    beforeAll(() => {
        const mockMatchMedia = () => {
            window.matchMedia = query => ({
              matches: false,
              media: query,
              onchange: null,
              addListener: jest.fn(),
              removeListener: jest.fn(),
              addEventListener: jest.fn(),
              removeEventListener: jest.fn(),
              dispatchEvent: jest.fn(),
            });
          };
          mockMatchMedia();
    });

    test("render login component", async () => {
        render(<Router>
              <LoginPage />
            </Router>
          );
          const buttonList = await screen.findAllByRole("button");
          expect(buttonList).toHaveLength(2);
    });

    test("password input should have type password", () => {
        const {container} = render(<Router>
            <LoginPage />
          </Router>);
        const inputs =  container.getElementsByClassName("ant-input")
        const password = inputs[1];
        expect(password.type).toEqual("password");
    });

    test("click login button", async () => {
        const mockLogin = jest.fn();
        const username = "david";
        const password = "david123";
        const {container} = render(<Router>
            <LoginPage onSubmit={mockLogin(username, password)}/>
          </Router>);

        const submitBtn = await screen.findByText("Prijavi se");
        const inputs = await container.getElementsByClassName("ant-input")
        const usernameInput = inputs[0];
        const passwordInput = inputs[1];

        await act( async () => {
            usernameInput.value = username;
            passwordInput.value = password;
            fireEvent.click(submitBtn.parentElement);
        });
        await expect(mockLogin).toHaveBeenCalled();
        await expect(mockLogin).toHaveBeenCalledTimes(1);
        await expect(mockLogin).toHaveBeenCalledWith("david", "david123");
    });
});

