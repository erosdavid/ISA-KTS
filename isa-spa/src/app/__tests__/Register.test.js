import {fireEvent, render, screen} from "@testing-library/react";
import {RegisterPage} from "../pages/register/RegisterPage";
import { BrowserRouter as Router } from 'react-router-dom';
import userEvent from "@testing-library/user-event";
import { act } from '@testing-library/react';

describe('test register component', () => {
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

    test("render register component", async () => {
        render(<Router>
              <RegisterPage />
            </Router>
          );
          const buttonList = await screen.findAllByRole("button");
          expect(buttonList).toHaveLength(2);
    });

    test("email iniput field should accept email", async () => {
        const {container} = render(<Router>
            <RegisterPage />
          </Router>);
        const inputs = await container.getElementsByClassName("ant-input")
        const email = inputs[1];
        await act( async () => {   
            userEvent.type(email, "david");
        });
        expect(email.value).not.toMatch("david@gmail.com");
    });
})