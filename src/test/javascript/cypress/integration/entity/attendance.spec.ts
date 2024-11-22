import { entityItemSelector } from '../../support/commands';
import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Attendance e2e test', () => {
  const attendancePageUrl = '/attendance';
  const attendancePageUrlPattern = new RegExp('/attendance(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const attendanceSample = { employeeId: 82687 };

  let attendance: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/attendances+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/attendances').as('postEntityRequest');
    cy.intercept('DELETE', '/api/attendances/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (attendance) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/attendances/${attendance.id}`,
      }).then(() => {
        attendance = undefined;
      });
    }
  });

  it('Attendances menu should load Attendances page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('attendance');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Attendance').should('exist');
    cy.url().should('match', attendancePageUrlPattern);
  });

  describe('Attendance page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(attendancePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Attendance page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/attendance/new$'));
        cy.getEntityCreateUpdateHeading('Attendance');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', attendancePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/attendances',
          body: attendanceSample,
        }).then(({ body }) => {
          attendance = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/attendances+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/attendances?page=0&size=20>; rel="last",<http://localhost/api/attendances?page=0&size=20>; rel="first"',
              },
              body: [attendance],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(attendancePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Attendance page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('attendance');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', attendancePageUrlPattern);
      });

      it('edit button click should load edit Attendance page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Attendance');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', attendancePageUrlPattern);
      });

      it('last delete button click should delete instance of Attendance', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('attendance').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', attendancePageUrlPattern);

        attendance = undefined;
      });
    });
  });

  describe('new Attendance page', () => {
    beforeEach(() => {
      cy.visit(`${attendancePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Attendance');
    });

    it('should create an instance of Attendance', () => {
      cy.get(`[data-cy="employeeId"]`).type('37561').should('have.value', '37561');

      cy.get(`[data-cy="inTime"]`).type('2024-11-21').should('have.value', '2024-11-21');

      cy.get(`[data-cy="outTime"]`).type('2024-11-20').should('have.value', '2024-11-20');

      cy.get(`[data-cy="note"]`).type('pixel').should('have.value', 'pixel');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        attendance = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', attendancePageUrlPattern);
    });
  });
});
